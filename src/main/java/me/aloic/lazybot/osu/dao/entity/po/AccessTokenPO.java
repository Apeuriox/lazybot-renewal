package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName(value = "token", autoResultMap = true)
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long qq_code;

    private Integer player_id;

    private String player_name;

    private String access_token;

    private Integer expires_in;

    private String refresh_token;

    private String default_mode;
    private Integer valid;
    private String avatar_url;

    private String star_moon_ruleset;


    public AccessTokenPO(String refresh_token,String access_token) {
        this.refresh_token = refresh_token;
        this.access_token = access_token;
        this.default_mode="osu";
    }

    public String toSimpleString()
    {
        return "[Lazybot] 该用户的绑定情况如下\nLazybot ID: "+id
                +"\n缓存的用户名: " + player_name
                +"\nBancho Osu ID: " + player_id
                +"\n默认查询模式: "+ default_mode;
    }
}
