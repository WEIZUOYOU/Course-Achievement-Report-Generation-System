package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@EqualsAndHashCode(callSuper = true)
public class LuckysheetData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;
    
    /** 工作表名称 */
    private String sheetName;
    
    /** 单元格数据 */
    private String cellData;
    
    /** 工作表索引 */
    private Integer sheetIndex;
    
    /** 工作表状态（0正常 1停用） */
    private String status;
    
    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;
    
    /** 备注 */
    private String remark;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("sheetName", getSheetName())
            .append("sheetIndex", getSheetIndex())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 