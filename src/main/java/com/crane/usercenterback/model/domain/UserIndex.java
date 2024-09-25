package com.crane.usercenterback.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户索引表，用于缓存预热
 *
 * @TableName user_index
 */
@TableName(value = "user_index")
@Data
public class UserIndex implements Serializable {
    /**
     * 索引id
     */
    @TableId(type = IdType.AUTO)
    private Long uiId;

    /**
     * 用户id
     */
    private Long uId;

    /**
     * 计数器，到达一千次后减半
     */
    private Integer uiCount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserIndex other = (UserIndex) that;
        return (this.getUiId() == null ? other.getUiId() == null : this.getUiId().equals(other.getUiId()))
                && (this.getUId() == null ? other.getUId() == null : this.getUId().equals(other.getUId()))
                && (this.getUiCount() == null ? other.getUiCount() == null : this.getUiCount().equals(other.getUiCount()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUiId() == null) ? 0 : getUiId().hashCode());
        result = prime * result + ((getUId() == null) ? 0 : getUId().hashCode());
        result = prime * result + ((getUiCount() == null) ? 0 : getUiCount().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", uiId=").append(uiId);
        sb.append(", uId=").append(uId);
        sb.append(", uiCount=").append(uiCount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}