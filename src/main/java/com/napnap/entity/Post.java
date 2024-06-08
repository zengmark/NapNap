package com.napnap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子表
 * @TableName tb_post
 */
@TableName(value ="tb_post")
@Data
public class Post implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发帖用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 图片地址
     */
    @TableField(value = "pictures")
    private String pictures;

    /**
     * 标签
     */
    @TableField(value = "tag")
    private String tag;

    /**
     * 点赞数
     */
    @TableField(value = "likes")
    private Long likes;

    /**
     * 帖子收藏数
     */
    @TableField(value = "collect_num")
    private Long collectNum;

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

    // JSON utility methods
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getTagList() {
        try {
            return objectMapper.readValue(tag, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTagList(List<String> tagList) {
        try {
            this.tag = objectMapper.writeValueAsString(tagList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPictureList() {
        try {
            return objectMapper.readValue(pictures, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPictureList(List<String> pictureList) {
        try {
            this.pictures = objectMapper.writeValueAsString(pictureList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        Post other = (Post) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
                && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
                && (this.getPictures() == null ? other.getPictures() == null : this.getPictures().equals(other.getPictures()))
                && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
                && (this.getLikes() == null ? other.getLikes() == null : this.getLikes().equals(other.getLikes()))
                && (this.getCollectNum() == null ? other.getCollectNum() == null : this.getCollectNum().equals(other.getCollectNum()))
                && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
                && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
                && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getPictures() == null) ? 0 : getPictures().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getLikes() == null) ? 0 : getLikes().hashCode());
        result = prime * result + ((getCollectNum() == null) ? 0 : getCollectNum().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", pictures=").append(pictures);
        sb.append(", tag=").append(tag);
        sb.append(", likes=").append(likes);
        sb.append(", collectNum=").append(collectNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}