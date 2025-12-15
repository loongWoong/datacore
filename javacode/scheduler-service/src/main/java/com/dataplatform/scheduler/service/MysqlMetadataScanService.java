package com.dataplatform.scheduler.service;

import com.alibaba.fastjson.JSON;
import com.dataplatform.scheduler.dto.MysqlScanTaskDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MysqlMetadataScanService {
    
    private static final Logger logger = LoggerFactory.getLogger(MysqlMetadataScanService.class);
    
    @Autowired
    private DatasourceService datasourceService;
    
    /**
     * 执行MySQL元数据扫描任务
     * 
     * @param taskDTO 任务配置
     * @return 扫描结果
     */
    public String executeScan(MysqlScanTaskDTO taskDTO) {
        Path tempDir = null;
        try {
            logger.info("开始执行MySQL元数据扫描任务, 任务ID: {}", taskDTO.getDatasourceId());
            
            // 从数据源服务获取连接信息
            Map<String, Object> datasourceInfo = datasourceService.getDatasourceById(taskDTO.getDatasourceId());
            if (datasourceInfo == null) {
                logger.error("获取数据源信息失败, 任务ID: {}", taskDTO.getDatasourceId());
                return "获取数据源信息失败";
            }
            
            logger.info("成功获取数据源信息, 数据源ID: {}", taskDTO.getDatasourceId());
            
            // 获取项目根目录
            Path projectRoot = Paths.get(System.getProperty("user.dir")).getParent();
            logger.info("项目根目录路径: {}", projectRoot.toString());
            
            // 创建临时目录
            tempDir = Files.createTempDirectory("dbt_scan_");
            logger.info("创建临时目录: {}", tempDir.toString());
            
            // 创建.dbt目录
            Path dbtDir = tempDir.resolve(".dbt");
            Files.createDirectories(dbtDir);
            logger.info("创建.dbt目录: {}", dbtDir.toString());
            
            // 构建命令和参数文件
            String profilesContent = generateProfilesYaml(datasourceInfo);
            logger.debug("生成profiles.yml内容: {}", profilesContent);
            
            // 保存profiles.yml文件到.dbt目录
            Path profilesPath = dbtDir.resolve("profiles.yml");
            Files.write(profilesPath, profilesContent.getBytes());
            logger.info("profiles.yml文件已保存到: {}", profilesPath.toString());
            
            // 设置输出文件路径（相对于项目根目录）
            String outputFileName = taskDTO.getOutputPath() != null ? taskDTO.getOutputPath() : "metadata_scan_result.yaml";
            logger.info("设置输出文件名: {}", outputFileName);
            
            // 构建命令，传递输出文件名作为参数
            ProcessBuilder processBuilder = new ProcessBuilder();
            String pythonScriptPath = projectRoot.resolve("javacode/meta-dbt/scan_mysql_metadata.py").toString();
            logger.info("Python脚本路径: {}", pythonScriptPath);
            processBuilder.command("python", pythonScriptPath, outputFileName);
            
            // 设置工作目录为项目根目录
            processBuilder.directory(projectRoot.toFile());
            logger.info("设置工作目录为: {}", projectRoot.toString());
            
            // 设置环境变量
            processBuilder.environment().put("DBT_PROFILES_DIR", tempDir.toString() + "/.dbt");
            logger.info("设置环境变量 DBT_PROFILES_DIR: {}", tempDir.toString() + "/.dbt");
            
            // 启动进程
            logger.info("启动Python进程执行MySQL元数据扫描...");
            Process process = processBuilder.start();
            
            // 等待进程完成
            int exitCode = process.waitFor();
            logger.info("Python进程执行完成, 退出码: {}", exitCode);
            
            if (exitCode == 0) {
                // 检查输出文件是否存在
                Path outputFile = projectRoot.resolve(outputFileName);
                logger.info("检查输出文件是否存在: {}", outputFile.toString());
                
                if (Files.exists(outputFile)) {
                    logger.info("输出文件存在，准备移动文件");
                    // 将输出文件移动到临时目录以便清理
                    Path tempOutputFile = tempDir.resolve(outputFileName);
                    Files.move(outputFile, tempOutputFile);
                    logger.info("MySQL元数据扫描任务执行成功，输出文件已生成并移动到: {}", tempOutputFile.toString());
                    return "MySQL元数据扫描任务执行成功，输出文件已生成";
                } else {
                    // 添加更详细的检查日志
                    logger.warn("未找到预期的输出文件: {}", outputFile.toString());
                    
                    // 检查项目根目录下的所有文件
                    try {
                        logger.info("项目根目录下的文件列表:");
                        Files.list(projectRoot).forEach(path -> 
                            logger.info("  文件: {}", path.getFileName().toString())
                        );
                    } catch (IOException e) {
                        logger.error("列出项目根目录文件时出错: {}", e.getMessage());
                    }
                    
                    // 检查Python脚本所在目录的文件
                    Path scriptDir = projectRoot.resolve("javacode/meta-dbt");
                    try {
                        logger.info("脚本目录下的文件列表:");
                        Files.list(scriptDir).forEach(path -> 
                            logger.info("  文件: {}", path.getFileName().toString())
                        );
                    } catch (IOException e) {
                        logger.error("列出脚本目录文件时出错: {}", e.getMessage());
                    }
                    
                    return "MySQL元数据扫描任务执行成功，但未找到输出文件";
                }
            } else {
                // 读取错误输出
                String errorOutput = readInputStream(process.getErrorStream());
                logger.error("MySQL元数据扫描任务执行失败，退出码: {}, 错误信息: {}", exitCode, errorOutput);
                return "MySQL元数据扫描任务执行失败，退出码: " + exitCode + ", 错误信息: " + errorOutput;
            }
        } catch (IOException | InterruptedException e) {
            logger.error("执行MySQL元数据扫描任务时发生错误: {}", e.getMessage(), e);
            return "执行MySQL元数据扫描任务时发生错误: " + e.getMessage();
        } finally {
            // 清理临时目录
            if (tempDir != null) {
                try {
                    logger.info("清理临时目录: {}", tempDir.toString());
                    Files.walk(tempDir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(java.io.File::delete);
                    logger.info("临时目录清理完成");
                } catch (IOException e) {
                    logger.error("清理临时目录失败: {}", e.getMessage());
                }
            }
        }
    }
    
    /**
     * 读取输入流内容
     * 
     * @param inputStream 输入流
     * @return 内容字符串
     * @throws IOException IO异常
     */
    private String readInputStream(java.io.InputStream inputStream) throws IOException {
        java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString("UTF-8");
    }
    
    /**
     * 生成profiles.yml内容
     * 
     * @param datasourceInfo 数据源信息
     * @return profiles.yml内容
     */
    private String generateProfilesYaml(Map<String, Object> datasourceInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("myproject:\n");
        sb.append("  outputs:\n");
        sb.append("    dev:\n");
        sb.append("      type: mysql\n");
        sb.append("      host: ").append(datasourceInfo.get("host")).append("\n");
        sb.append("      port: ").append(datasourceInfo.get("port")).append("\n");
        sb.append("      user: ").append(datasourceInfo.get("username")).append("\n");
        sb.append("      password: ").append(datasourceInfo.get("password")).append("\n");
        sb.append("      database: ").append(datasourceInfo.get("databaseName")).append("\n");
        sb.append("  target: dev\n");
        return sb.toString();
    }
    
    // Getter for datasourceId
    public static class MysqlScanTaskDTO {
        private Long datasourceId;
        private String outputPath = "metadata_scan_result.yaml";
        private Integer executorTimeout = 300;
        private Integer executorFailRetryCount = 0;
        
        // Getters and setters
        public Long getDatasourceId() {
            return datasourceId;
        }
        
        public void setDatasourceId(Long datasourceId) {
            this.datasourceId = datasourceId;
        }
        
        public String getOutputPath() {
            return outputPath;
        }
        
        public void setOutputPath(String outputPath) {
            this.outputPath = outputPath;
        }
        
        public Integer getExecutorTimeout() {
            return executorTimeout;
        }
        
        public void setExecutorTimeout(Integer executorTimeout) {
            this.executorTimeout = executorTimeout;
        }
        
        public Integer getExecutorFailRetryCount() {
            return executorFailRetryCount;
        }
        
        public void setExecutorFailRetryCount(Integer executorFailRetryCount) {
            this.executorFailRetryCount = executorFailRetryCount;
        }
    }
}