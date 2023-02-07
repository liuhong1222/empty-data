package com.zhongzhi.data.service.front;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.zhongzhi.data.api.ApiResult;
import com.zhongzhi.data.constants.Constant;
import com.zhongzhi.data.constants.RedisConstant;
import com.zhongzhi.data.entity.sys.FileUpload;
import com.zhongzhi.data.enums.ApiCode;
import com.zhongzhi.data.enums.ProductTypeEnum;
import com.zhongzhi.data.exception.BusinessException;
import com.zhongzhi.data.param.UploadFileStatusParam;
import com.zhongzhi.data.redis.DistributedLockWrapper;
import com.zhongzhi.data.redis.RedisClient;
import com.zhongzhi.data.service.sys.FileUploadService;
import com.zhongzhi.data.util.*;
import com.zhongzhi.data.vo.CustomerInfoVo;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

/**
 * 文件分片上传实现类
 * @author liuh
 * @date 2021年11月4日
 */
@Slf4j
@Service
public class ChunkService {
	
	@Value("${file.upload.path}")
	private String fileUploadPath;
	
	@Autowired
	private JedisPool jedisPool;
	
	@Autowired
	private FileUploadService fileUploadService;
	
	@Autowired
	private Snowflake snowflake;
	
	@Autowired
	private RedisClient redisClient;
	
	private static ExecutorService executorService = Executors.newFixedThreadPool(6);
	
	public ApiResult upload(MultipartFile file, String md5, String name, int chunks, int chunk,Long chunkSize) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		String md5String = redisClient.get(String.format(RedisConstant.FILE_MD5_CACHE_KEY, customer.getCustomerId(),md5));
		if(StringUtils.isNotBlank(md5String)) {
			return ApiResult.fail(String.format("该文件与文件[%s]内容一致，已检测成功，请勿重复检测", md5String));
		}
		
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				uploadFileChunk(customer,file, md5, name, chunks, chunk,chunkSize);
			}
		});
		
		return ApiResult.ok();
	}
	
	public ApiResult uploadStatus(UploadFileStatusParam param) {
		// 获取用户信息
		CustomerInfoVo customer = ThreadLocalContainer.getCustomerInfo();
		try {
			// 检查是否已经全部上传完成
			String filePath = unionFileChunks(customer.getCustomerId(), param.getMd5(), param.getFileName(), param.getChunks());
			if(StringUtils.isBlank(filePath)) {
				return ApiResult.fail(ApiCode.FILE_UPLOADING);
			}
			
			// 检测上传文件数量
			int lineNum = getLineNum(customer.getCustomerId(), param.getMd5(), param.getFileName(), filePath);
			Integer minLineNum = getMinLineNum(param.getProductCode());
			if(lineNum < minLineNum) {
				return ApiResult.fail(String.format("请上传不少于%s个号码的文件", minLineNum));
			}
			
			if(lineNum > Constant.MAX_LINE_NUM) {
				return ApiResult.fail(String.format("请上传不多于%s个号码的文件", Constant.MAX_LINE_NUM));
			}
			
			FileUpload fileUpload = new FileUpload();
			fileUpload.setId(snowflake.nextId());
			fileUpload.setCustomerId(customer.getCustomerId());
			fileUpload.setFileName(param.getFileName().substring(0,param.getFileName().lastIndexOf(".")));
			fileUpload.setFileRows(lineNum);
			fileUpload.setFileUploadUrl(filePath.replace(".xls", ".txt"));
			fileUpload.setFileMd5(param.getMd5());
			fileUpload.setFileType(param.getProductCode());
			fileUpload.setFileSize(String.valueOf(new File(filePath).length()));
			int counts = fileUploadService.saveOne(fileUpload);
			if(counts != 1) {
				log.error("{}, 文件分片上传失败，上传记录入库失败，param:{}",customer.getCustomerId(),JSON.toJSONString(param));
				
				cn.hutool.core.io.FileUtil.del(filePath.replace(".xls", ".txt"));
				return ApiResult.fail("系统异常，请重新上传");
			}
			
			log.info("{}， 文件上传成功，param:{}",customer.getCustomerId(),JSON.toJSONString(param));
			return ApiResult.ok(fileUpload.getId().toString());
		} catch (Exception e) {
			log.error("{}，文件上传异常，param:{},info:",customer.getCustomerId(),JSON.toJSONString(param),e);
			return ApiResult.fail("系统异常，请重新上传");
		}
	}
	
	private String unionFileChunks(Long customerId,String md5,String fileRealName,Integer chunks) {
		//1.设置redis锁
        DistributedLockWrapper lock = new DistributedLockWrapper(jedisPool, String.format(RedisConstant.UPLOAD_UNION_CHUNKS_KEY, 
        		customerId,md5), 5* 1000L, 1000 * 5);
		try {
	        if (StringUtils.isBlank(lock.getIdentifier())) {
	        	log.error("{}, 分片文件正在合并中，md5:{},fileRealName:{}",customerId,md5,fileRealName);
	        	return null;
	        }
	        
	        String foldName = fileUploadPath + "temp/" + DateUtils.getDate() + "/" + customerId + "/";
	        // 获取分片文件名称
	        String fileName = getFileName(customerId, md5, fileRealName);
	        File destFile = new File(foldName + fileName);
	        // 检查文件是否已经存在，防止前端忽略第一次请求的情况
	        if (destFile.exists()) {
	            lock.releaseLock();
	            return foldName + fileName;
	        }
	        
	        // 检查分片文件是否都存在
	        for (int chunk = 0; chunk < chunks; chunk++) {
	            String chunkFileName = getChunkFileName(customerId, md5, fileRealName, chunks, chunk);
	            if (!new File(foldName + chunkFileName).exists()) {
	                lock.releaseLock();
	                return null;
	            }
	        }
	        
	        FileOutputStream fileOutputStream = new FileOutputStream(destFile);
            for (int chunk = 0; chunk < chunks; chunk++) {
                String chunkFileName = getChunkFileName(customerId, md5, fileRealName, chunks, chunk);
                File chunkFile = new File(foldName + chunkFileName);

                FileInputStream fileInputStream = new FileInputStream(chunkFile);
                FileChannel fileChannel = fileInputStream.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
                while (fileChannel.read(byteBuffer) > 0) {
                    fileOutputStream.write(byteBuffer.array());
                }
                
                fileChannel.close();
                fileInputStream.close();
                chunkFile.delete();
            }
            
            fileOutputStream.flush();
            fileOutputStream.close();
            
            lock.releaseLock();
            log.info("{}, 分片上传文件合并完成，md5:{},fileRealName:{},filePath:{}",customerId,md5,fileRealName,foldName + fileName);
            return foldName + fileName;
		} catch (Exception e) {
			log.error("{}, 分片上传文件合并异常，md5:{},fileRealName:{},info:",customerId,md5,fileRealName,e);
			lock.releaseLock();
			return null;
		}
	}
	
	 /**
     * 获取上传文件条数
     *
     * @param accountName       账号名称
     * @param productUploadEnum 上传文件枚举
     * @param md5               上传文件MD5
     * @return 上传文件条数
     */
    private Integer getLineNum(Long customerId, String md5, String fileName,String filePath) throws BusinessException {
        int lineNum = 0;
        String ext = fileName.substring(fileName.lastIndexOf("."));
        switch (ext) {
            // excel文件
            case ".xls":
                lineNum = ExcelUtil.getLineNum(filePath, 0,filePath.replace(".xls", ".txt"));
                break;
            case ".txt":
            	lineNum = FileUtil.getFileLineNum(filePath);
            	break;
            default:
                log.error("{}，不支持的文件格式，fileName:{},filePath:{}",customerId,fileName,filePath);
        }
        
        return lineNum;
    }

	/**
     * 上传文件分片
     *
     * @param productUploadEnum 上传产品枚举
     * @param accountName       账号名称
     * @param file              上传文件信息
     * @param md5               上传文件MD5
     * @param name              上传文件名称
     * @param chunks            文件分片数量
     * @param chunk             当前文件分片
     */
    private void uploadFileChunk(CustomerInfoVo customer,MultipartFile file, String md5, String name, int chunks, int chunk,Long chunkSize) {
        log.info("uploadFileChunk - [开始上传] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]", customer.getCustomerId(), md5,name, chunks, chunk);
        String foldName = fileUploadPath + "temp/" + DateUtils.getDate() + "/" + customer.getCustomerId() + "/";
        // 获取分片文件名称
        String chunkFileName = getChunkFileName(customer.getCustomerId(), md5, name, chunks, chunk);
        File chunkFile = new File(foldName + chunkFileName);

        // 分片文件如果已存在，则不再上传
        if (chunkFile.exists()) {
            log.info("uploadFileChunk - [分片已存在，停止上传] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]", customer.getCustomerId(), md5, name,chunks, chunk);
            return;
        }

        // 获取临时文件名称
        String chunkFileNameOnUpload = getChunkFileNameOnUpload(customer.getCustomerId(), md5, name, chunks, chunk);
        String chunkFilePathOnUpload = foldName + chunkFileNameOnUpload;
        File chunkFileOnUpload = new File(chunkFilePathOnUpload);
        try {
            File parent = chunkFileOnUpload.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(chunkFileOnUpload));
            bos.write(file.getBytes());
            bos.close();
            log.info("uploadFileChunk - [上传临时文件完成] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]",customer.getCustomerId(), md5,name, chunks, chunk);
            // 文件名称修改为完成时的文件名称
            chunkFileOnUpload.renameTo(chunkFile);
            log.info("uploadFileChunk - [上传完成] - [账号:{},md5:{},name:{},chunks:{},chunk:{}]", customer.getCustomerId(), md5,name, chunks, chunk);
        } catch (IOException e) {
        	chunkFileOnUpload.delete();
            log.error("uploadFileChunk - [上传分片文件失败] - [账号:{},md5:{},name:{},chunks:{},chunk:{},文件地址:{}] - info:",
            		customer.getCustomerId(), md5,name, chunks, chunk, chunkFilePathOnUpload, e);
        }
    }
    
    /**
     * 获取上传文件分片名称（正在上传）
     */
    private String getChunkFileNameOnUpload(Long customerId, String md5, String name, int chunks, int chunk) {
        return String.format("%s.uploading", getChunkFileName(customerId, md5, name, chunks, chunk));
    }
    
    /**
     * 获取上传文件分片名称
     */
    private String getChunkFileName(Long customerId, String md5, String name, int chunks, int chunk) {
        return String.format("%s.%s_%s", getFileName(customerId, md5, name), chunks, chunk);
    }
    
    /**
     * 获取上传文件名称
     */
    private String getFileName(Long customerId, String md5, String name) {
        return String.format("/%s_%s.%s", md5.substring(8, 24), customerId, name.substring(name.lastIndexOf(".") + 1));
    }
    
    private Integer getMinLineNum(Integer productCode) {
    	return ProductTypeEnum.REALTIME.getCode()==productCode?Constant.REALTIME_MIN_LINE_NUM:
    		((ProductTypeEnum.EMPTY.getCode()==productCode)?Constant.EMPTY_MIN_LINE_NUM:Constant.INTERNATIONAL_MIN_LINE_NUM);
    }
}
