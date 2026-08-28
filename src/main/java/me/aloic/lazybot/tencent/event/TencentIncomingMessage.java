package me.aloic.lazybot.tencent.event;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class TencentIncomingMessage
{
    private TencentScene scene;
    private String eventType;
    private String eventId;
    private String messageId;
    private String content;
    private String userOpenid;
    private String groupOpenid;
    private String timestamp;

    public boolean isGroup()
    {
        return scene == TencentScene.GROUP;
    }

    public String targetOpenid()
    {
        return isGroup() ? groupOpenid : userOpenid;
    }

    public static TencentIncomingMessage fromDispatch(String eventType, String eventId, JSONObject data)
    {
        TencentIncomingMessage message = new TencentIncomingMessage();
        message.setEventType(eventType);
        message.setEventId(eventId);
        if (data == null) {
            return message;
        }
        message.setMessageId(data.getString("id"));
        message.setContent(data.getString("content"));
        message.setTimestamp(data.getString("timestamp"));
        message.setGroupOpenid(firstNonBlank(
                data.getString("group_openid"),
                data.getString("group_id")));

        JSONObject author = data.getJSONObject("author");
        String memberOpenid = author == null ? null : author.getString("member_openid");
        String userOpenid = author == null ? null : author.getString("user_openid");
        String authorId = author == null ? null : author.getString("id");

        if (message.getGroupOpenid() != null
                || "GROUP_AT_MESSAGE_CREATE".equals(eventType)
                || "GROUP_MESSAGE_CREATE".equals(eventType)
                || "GROUP_ADD_ROBOT".equals(eventType)) {
            message.setScene(TencentScene.GROUP);
            message.setUserOpenid(firstNonBlank(
                    memberOpenid,
                    authorId,
                    userOpenid,
                    data.getString("op_member_openid")));
        }
        else {
            message.setScene(TencentScene.C2C);
            message.setUserOpenid(firstNonBlank(
                    userOpenid,
                    authorId,
                    memberOpenid,
                    data.getString("openid")));
        }
        return message;
    }

    private static String firstNonBlank(String... values)
    {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
