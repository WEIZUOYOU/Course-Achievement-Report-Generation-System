package com.ruoyi.common.utils.report;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Word报告生成工具类
 * 
 * @author ruoyi
 */
public class WordReportGenerator {
    
    private static final Logger log = LoggerFactory.getLogger(WordReportGenerator.class);
    
    /**
     * 生成课程目标达成评价报告
     * 
     * @param title 报告标题
     * @param courseInfo 课程信息
     * @param statisticsData 统计数据
     * @param achievementData 达成度数据
     * @param chartsDir 图表目录
     * @param outputFile 输出文件
     * @return 是否生成成功
     */
    public static boolean generateReport(String title, Map<String, String> courseInfo,
                                        Map<String, Object> statisticsData,
                                        List<Map<String, Object>> achievementData,
                                        String chartsDir, String outputFile) {
        try (XWPFDocument document = new XWPFDocument()) {
            // 1. 添加标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            
            // 2. 添加课程信息
            XWPFParagraph courseInfoParagraph = document.createParagraph();
            courseInfoParagraph.setAlignment(ParagraphAlignment.LEFT);
            for (Map.Entry<String, String> entry : courseInfo.entrySet()) {
                XWPFRun infoRun = courseInfoParagraph.createRun();
                infoRun.setText(entry.getKey() + ": " + entry.getValue());
                infoRun.addBreak();
                infoRun.setFontSize(12);
            }
            
            // 3. 添加成绩分布图
            addImage(document, new File(chartsDir + "/grade_distribution_chart.png"), 
                    "成绩分布图", 500, 300);
            
            // 4. 添加课程目标达成度柱状图
            addImage(document, new File(chartsDir + "/achievement_bar_chart.png"), 
                    "课程目标达成度柱状图", 500, 300);
            
            // 5. 添加课程目标散点图
            File dir = new File(chartsDir);
            File[] files = dir.listFiles((d, name) -> name.endsWith("_achievement_scatter_chart.png"));
            if (files != null) {
                for (File file : files) {
                    String targetName = file.getName().replace("_achievement_scatter_chart.png", "");
                    addImage(document, file, targetName + " 达成度散点图", 500, 300);
                }
            }
            
            // 6. 添加总体达成情况表
            addAchievementTable(document, achievementData);
            
            // 7. 添加统计数据
            addStatisticsSection(document, statisticsData);
            
            // 8. 保存文档
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                document.write(out);
            }
            
            return true;
        } catch (Exception e) {
            log.error("生成Word报告失败", e);
            return false;
        }
    }
    
    // 添加图片
    private static void addImage(XWPFDocument document, File imageFile, String caption, 
                               int width, int height) throws Exception {
        if (!imageFile.exists()) {
            return;
        }
        
        // 添加图片
        XWPFParagraph imageParagraph = document.createParagraph();
        imageParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun imageRun = imageParagraph.createRun();
        
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            imageRun.addPicture(fis, Document.PICTURE_TYPE_PNG, imageFile.getName(),
                    Units.toEMU(width), Units.toEMU(height));
        }
        
        // 添加图片标题
        XWPFParagraph captionParagraph = document.createParagraph();
        captionParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun captionRun = captionParagraph.createRun();
        captionRun.setText("图 " + caption);
        captionRun.setFontSize(11);
        captionRun.setBold(true);
        
        // 添加空行
        document.createParagraph();
    }
    
    // 添加课程目标达成情况表
    private static void addAchievementTable(XWPFDocument document, 
                                          List<Map<String, Object>> achievementData) {
        if (achievementData == null || achievementData.isEmpty()) {
            return;
        }
        
        // 创建表格标题
        XWPFParagraph tableTitleParagraph = document.createParagraph();
        tableTitleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun tableTitleRun = tableTitleParagraph.createRun();
        tableTitleRun.setText("表 课程目标达成情况");
        tableTitleRun.setBold(true);
        tableTitleRun.setFontSize(12);
        
        // 创建表格
        XWPFTable table = document.createTable();
        CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
        width.setType(STTblWidth.DXA);
        width.setW(BigInteger.valueOf(9000));
        
        // 添加表头
        XWPFTableRow headerRow = table.getRow(0);
        headerRow.getCell(0).setText("课程目标");
        headerRow.addNewTableCell().setText("考试_额定值");
        headerRow.addNewTableCell().setText("考试_分值");
        headerRow.addNewTableCell().setText("考试_达成度");
        headerRow.addNewTableCell().setText("课堂作业_额定值");
        headerRow.addNewTableCell().setText("课堂作业_分值");
        headerRow.addNewTableCell().setText("课堂作业_达成度");
        headerRow.addNewTableCell().setText("实验报告_额定值");
        headerRow.addNewTableCell().setText("实验报告_分值");
        headerRow.addNewTableCell().setText("实验报告_达成度");
        headerRow.addNewTableCell().setText("达成度(标准=0.6)");
        
        // 添加数据行
        for (Map<String, Object> rowData : achievementData) {
            XWPFTableRow dataRow = table.createRow();
            int cellIndex = 0;
            
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("课程目标")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("考试_额定值")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("考试_分值")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("考试_达成度")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("课堂作业_额定值")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("课堂作业_分值")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("课堂作业_达成度")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("实验报告_额定值")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("实验报告_分值")));
            dataRow.getCell(cellIndex++).setText(String.valueOf(rowData.get("实验报告_达成度")));
            dataRow.getCell(cellIndex).setText(String.valueOf(rowData.get("达成度（标准= 0.6）")));
        }
        
        // 添加空行
        document.createParagraph();
    }
    
    // 添加统计数据部分
    private static void addStatisticsSection(XWPFDocument document, Map<String, Object> statistics) {
        if (statistics == null || statistics.isEmpty()) {
            return;
        }
        
        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText("统计数据");
        titleRun.setBold(true);
        titleRun.setFontSize(14);
        
        // 添加总成绩统计
        if (statistics.containsKey("总成绩")) {
            Map<String, Object> scoreStats = (Map<String, Object>) statistics.get("总成绩");
            
            XWPFParagraph scoreParagraph = document.createParagraph();
            scoreParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun scoreRun = scoreParagraph.createRun();
            scoreRun.setText("总成绩统计:");
            scoreRun.setBold(true);
            scoreRun.setFontSize(12);
            
            XWPFParagraph detailsParagraph = document.createParagraph();
            detailsParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun detailsRun = detailsParagraph.createRun();
            detailsRun.setText(String.format("平均值: %.2f, 中位值: %.2f, 标准差: %.2f, 最高分: %.2f, 最低分: %.2f",
                    Double.parseDouble(scoreStats.get("平均值").toString()),
                    Double.parseDouble(scoreStats.get("中位值").toString()),
                    Double.parseDouble(scoreStats.get("标准差").toString()),
                    Double.parseDouble(scoreStats.get("最高分").toString()),
                    Double.parseDouble(scoreStats.get("最低分").toString())));
            detailsRun.setFontSize(11);
        }
        
        // 添加人数统计
        if (statistics.containsKey("人数统计")) {
            Map<String, Object> countStats = (Map<String, Object>) statistics.get("人数统计");
            
            XWPFParagraph countParagraph = document.createParagraph();
            countParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun countRun = countParagraph.createRun();
            countRun.setText("人数统计:");
            countRun.setBold(true);
            countRun.setFontSize(12);
            
            XWPFParagraph detailsParagraph = document.createParagraph();
            detailsParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun detailsRun = detailsParagraph.createRun();
            detailsRun.setText(String.format("总人数: %d, 参加考试人数: %d, 未参加考试人数: %d",
                    Integer.parseInt(countStats.get("总人数").toString()),
                    Integer.parseInt(countStats.get("参加考试人数").toString()),
                    Integer.parseInt(countStats.get("未参加考试人数").toString())));
            detailsRun.setFontSize(11);
        }
        
        // 添加成绩分布
        if (statistics.containsKey("成绩分布")) {
            Map<String, Object> gradeStats = (Map<String, Object>) statistics.get("成绩分布");
            
            XWPFParagraph gradeParagraph = document.createParagraph();
            gradeParagraph.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun gradeRun = gradeParagraph.createRun();
            gradeRun.setText("成绩分布:");
            gradeRun.setBold(true);
            gradeRun.setFontSize(12);
            
            String[] grades = {"优秀", "良好", "中等", "及格", "不及格"};
            for (String grade : grades) {
                if (gradeStats.containsKey(grade)) {
                    Map<String, Object> gradeData = (Map<String, Object>) gradeStats.get(grade);
                    
                    XWPFParagraph detailsParagraph = document.createParagraph();
                    detailsParagraph.setAlignment(ParagraphAlignment.LEFT);
                    XWPFRun detailsRun = detailsParagraph.createRun();
                    detailsRun.setText(String.format("%s: %d人, 占比: %.2f%%",
                            grade,
                            Integer.parseInt(gradeData.get("人数").toString()),
                            Double.parseDouble(gradeData.get("占比").toString())));
                    detailsRun.setFontSize(11);
                }
            }
        }
    }
} 