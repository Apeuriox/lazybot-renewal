package me.aloic.lazybot.osu.dao.entity.dto.ppDetails;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author: huhao109
 * 2024/4/18 16:21
 */
@Data
@NoArgsConstructor
public class RosuppRecordDTO implements Serializable {
    private List<AccPPlist> accPPlist;
    private Double iffc;
    private DetailAttrs detailAttrs;
}
