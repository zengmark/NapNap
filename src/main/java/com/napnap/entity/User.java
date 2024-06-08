package com.napnap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 * @TableName tb_user
 */
@TableName(value ="tb_user")
@Data
public class User implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField(value = "user_name")
    private String userName;

    /**
     * 用户账号
     */
    @TableField(value = "user_account")
    private String userAccount;

    /**
     * 用户密码
     */
    @TableField(value = "user_password")
    private String userPassword;

    /**
     * 用户头像
     */
    @TableField(value = "user_avatar")
    private String userAvatar;

    /**
     * 粉丝数量
     */
    @TableField(value = "fans_num")
    private Long fansNum;

    /**
     * 关注数量
     */
    @TableField(value = "focus_num")
    private Long focusNum;

    /**
     * 用户简介
     */
    @TableField(value = "user_profile")
    private String userProfile;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;

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
        User other = (User) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserName() == null ? other.getUserName() == null : this.getUserName().equals(other.getUserName()))
                && (this.getUserAccount() == null ? other.getUserAccount() == null : this.getUserAccount().equals(other.getUserAccount()))
                && (this.getUserPassword() == null ? other.getUserPassword() == null : this.getUserPassword().equals(other.getUserPassword()))
                && (this.getUserAvatar() == null ? other.getUserAvatar() == null : this.getUserAvatar().equals(other.getUserAvatar()))
                && (this.getFansNum() == null ? other.getFansNum() == null : this.getFansNum().equals(other.getFansNum()))
                && (this.getFocusNum() == null ? other.getFocusNum() == null : this.getFocusNum().equals(other.getFocusNum()))
                && (this.getUserProfile() == null ? other.getUserProfile() == null : this.getUserProfile().equals(other.getUserProfile()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserName() == null) ? 0 : getUserName().hashCode());
        result = prime * result + ((getUserAccount() == null) ? 0 : getUserAccount().hashCode());
        result = prime * result + ((getUserPassword() == null) ? 0 : getUserPassword().hashCode());
        result = prime * result + ((getUserAvatar() == null) ? 0 : getUserAvatar().hashCode());
        result = prime * result + ((getFansNum() == null) ? 0 : getFansNum().hashCode());
        result = prime * result + ((getFocusNum() == null) ? 0 : getFocusNum().hashCode());
        result = prime * result + ((getUserProfile() == null) ? 0 : getUserProfile().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userName=").append(userName);
        sb.append(", userAccount=").append(userAccount);
        sb.append(", userPassword=").append(userPassword);
        sb.append(", userAvatar=").append(userAvatar);
        sb.append(", fansNum=").append(fansNum);
        sb.append(", focusNum=").append(focusNum);
        sb.append(", userProfile=").append(userProfile);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}