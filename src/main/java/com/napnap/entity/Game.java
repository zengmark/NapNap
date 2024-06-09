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
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 游戏表
 * @TableName tb_game
 */
@TableName(value ="tb_game")
@Data
public class Game implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 游戏名称
     */
    @TableField(value = "game_name")
    private String gameName;

    /**
     * 游戏简介
     */
    @TableField(value = "game_profile")
    private String gameProfile;

    /**
     * 游戏图片
     */
    @TableField(value = "game_icon")
    private String gameIcon;

    /**
     * 标签
     */
    @TableField(value = "tag")
    private String tag;

    /**
     * 评分
     */
    @TableField(value = "game_score")
    private BigDecimal gameScore;

    /**
     * 游戏大小
     */
    @TableField(value = "game_size")
    private BigDecimal gameSize;

    /**
     * 游戏链接
     */
    @TableField(value = "game_url")
    private String gameUrl;

    /**
     * 收藏数
     */
    @TableField(value = "collect_num")
    private Long collectNum;

    /**
     * 下载量
     */
    @TableField(value = "download_num")
    private Long downloadNum;

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

    public List<String> getTagList(){
        try {
            return objectMapper.readValue(tag, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTagList(List<String> tagList){
        try {
            this.tag = objectMapper.writeValueAsString(tagList);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<String> getGameIconList() {
        try {
            return objectMapper.readValue(gameIcon, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setGameIconList(List<String> gameIconList) {
        try {
            this.gameIcon = objectMapper.writeValueAsString(gameIconList);
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
        Game other = (Game) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getGameName() == null ? other.getGameName() == null : this.getGameName().equals(other.getGameName()))
            && (this.getGameProfile() == null ? other.getGameProfile() == null : this.getGameProfile().equals(other.getGameProfile()))
            && (this.getGameIcon() == null ? other.getGameIcon() == null : this.getGameIcon().equals(other.getGameIcon()))
            && (this.getTag() == null ? other.getTag() == null : this.getTag().equals(other.getTag()))
            && (this.getGameScore() == null ? other.getGameScore() == null : this.getGameScore().equals(other.getGameScore()))
            && (this.getGameSize() == null ? other.getGameSize() == null : this.getGameSize().equals(other.getGameSize()))
            && (this.getGameUrl() == null ? other.getGameUrl() == null : this.getGameUrl().equals(other.getGameUrl()))
            && (this.getCollectNum() == null ? other.getCollectNum() == null : this.getCollectNum().equals(other.getCollectNum()))
            && (this.getDownloadNum() == null ? other.getDownloadNum() == null : this.getDownloadNum().equals(other.getDownloadNum()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getGameName() == null) ? 0 : getGameName().hashCode());
        result = prime * result + ((getGameProfile() == null) ? 0 : getGameProfile().hashCode());
        result = prime * result + ((getGameIcon() == null) ? 0 : getGameIcon().hashCode());
        result = prime * result + ((getTag() == null) ? 0 : getTag().hashCode());
        result = prime * result + ((getGameScore() == null) ? 0 : getGameScore().hashCode());
        result = prime * result + ((getGameSize() == null) ? 0 : getGameSize().hashCode());
        result = prime * result + ((getGameUrl() == null) ? 0 : getGameUrl().hashCode());
        result = prime * result + ((getCollectNum() == null) ? 0 : getCollectNum().hashCode());
        result = prime * result + ((getDownloadNum() == null) ? 0 : getDownloadNum().hashCode());
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
        sb.append(", gameName=").append(gameName);
        sb.append(", gameProfile=").append(gameProfile);
        sb.append(", gameIcon=").append(gameIcon);
        sb.append(", tag=").append(tag);
        sb.append(", gameScore=").append(gameScore);
        sb.append(", gameSize=").append(gameSize);
        sb.append(", gameUrl=").append(gameUrl);
        sb.append(", collectNum=").append(collectNum);
        sb.append(", downloadNum=").append(downloadNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}