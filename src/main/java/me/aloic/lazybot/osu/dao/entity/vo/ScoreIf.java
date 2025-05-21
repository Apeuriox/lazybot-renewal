package me.aloic.lazybot.osu.dao.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreIf implements Serializable
{
    public Double pp;
    public Integer index;

    public ScoreIf(Double pp) {
        this.pp = pp;
    }

    @Override
    public String toString()
    {
        return "ScoreIf{" +
                "pp=" + pp +
                ", index=" + index +
                '}';
    }
}
