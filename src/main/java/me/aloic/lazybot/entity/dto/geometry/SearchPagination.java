package me.aloic.lazybot.entity.dto.geometry;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * GD搜索分页信息
 */
@Data
@NoArgsConstructor
public class SearchPagination implements Serializable
{
    /** 搜索结果总数 */
    private String total;
    /** 偏移量 */
    private String offset;
    /** 当前页数量 */
    private String amount;
}
