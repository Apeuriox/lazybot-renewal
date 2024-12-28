package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
public class AccPPlist implements Serializable
{
    private Integer acc;
    private Double pp;
}
