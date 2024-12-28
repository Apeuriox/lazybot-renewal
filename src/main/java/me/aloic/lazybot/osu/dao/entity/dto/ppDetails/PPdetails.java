package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PPdetails implements Serializable
{
    private AccPPlist[] accPPlist;
    private Double iffc;
    private DetailAttrs detailAttrs;

}
