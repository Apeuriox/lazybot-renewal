package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@TableName(value = "authorized_user", autoResultMap = true)
@Data
@NoArgsConstructor
public class AuthorityPO implements Serializable
{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long qq_code;
    private Integer level_of_authority;
}
