package me.aloic.lazybot.osu.dao.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@AllArgsConstructor
@Data
@TableName(value = "user_token_discord", autoResultMap = true)
public class UserTokenPO implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Long discord_code;

    private Integer player_id;

    private String player_name;

    private String access_token;

    private String refresh_token;

    private Integer default_mode;

    public UserTokenPO(Long discord_code, Integer player_id, String player_name) {
        this.discord_code = discord_code;
        this.player_id = player_id;
        this.player_name = player_name;
        this.default_mode=0;
    }
    public UserTokenPO(Integer player_id, String player_name) {
        this.player_id = player_id;
        this.player_name = player_name;
        this.default_mode=0;
    }
    public UserTokenPO(String refresh_token,String access_token) {
        this.refresh_token = refresh_token;
        this.access_token = access_token;
        this.default_mode=0;
    }

    @Override
    public String toString()
    {
        return "UserTokenPO{" +
                "id=" + id +
                ", discord_code=" + discord_code +
                ", player_id=" + player_id +
                ", player_name='" + player_name + '\'' +
                ", access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", default_mode=" + default_mode +
                '}';
    }
}
