package me.aloic.lazybot.util;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.BeatmapsetDTO;
import me.aloic.lazybot.osu.dao.entity.dto.beatmap.ScoreLazerDTO;
import me.aloic.lazybot.osu.dao.entity.dto.osuTrack.HitScore;
import me.aloic.lazybot.osu.dao.entity.dto.player.PlayerInfoDTO;
import me.aloic.lazybot.osu.dao.entity.optionalattributes.beatmap.Mod;
import me.aloic.lazybot.osu.dao.entity.po.BeatmapCompactPO;
import me.aloic.lazybot.osu.dao.entity.vo.*;
import me.aloic.lazybot.osu.utils.AssertDownloadUtil;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *将osu api中返回的对象转换为最终需要发送消息的对象（参数的精简）
 */
public class TransformerUtil
{
    static TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public static PlayerInfoVO userTransform(PlayerInfoDTO playerInfoDTO) {
        PlayerInfoVO playerInfoVO = new PlayerInfoVO();
        playerInfoVO.setPerformancePoint(playerInfoDTO.getStatistics().getPp());
        playerInfoVO.setAccuracy(playerInfoDTO.getStatistics().getHit_accuracy());
        playerInfoVO.setCountry(playerInfoDTO.getCountry().getName());
        playerInfoVO.setCountryRank(playerInfoDTO.getStatistics().getCountry_rank());
        playerInfoVO.setPlayCount(playerInfoDTO.getStatistics().getPlay_count());
        playerInfoVO.setGlobalRank(playerInfoDTO.getStatistics().getGlobal_rank());
        playerInfoVO.setRankTotalScore(playerInfoDTO.getStatistics().getRanked_score());
        playerInfoVO.setTotalHitCount(playerInfoDTO.getStatistics().getTotal_hits());
        playerInfoVO.setPlayerName(playerInfoDTO.getUsername());
        playerInfoVO.setTotalPlayTime(playerInfoDTO.getStatistics().getPlay_time());
        playerInfoVO.setAvatarUrl(playerInfoDTO.getAvatar_url());
        playerInfoVO.setPrimaryColor(Optional.ofNullable(playerInfoDTO.getProfile_hue()).orElse(333));
        return playerInfoVO;

    }
    public static ScoreVO[] scoreTransformForArray(List<ScoreLazerDTO> scoreDTO) {
        ScoreVO[] scoreVO=new ScoreVO[scoreDTO.size()];
        for(int i=0;i<scoreDTO.size();i++)
        {
            scoreVO[i]=new ScoreVO();
            scoreVO[i].setScore(scoreDTO.get(i).getClassic_total_score());
            scoreVO[i].setAccuracy(scoreDTO.get(i).getAccuracy());
            scoreVO[i].setMods(scoreDTO.get(i).getMods().stream()
                    .map(Mod::getAcronym)
                    .toArray(String[]::new));
            scoreVO[i].setModJSON(scoreDTO.get(i).getMods());
            scoreVO[i].setCreate_at(scoreDTO.get(i).getEnded_at());
            scoreVO[i].setMaxCombo(scoreDTO.get(i).getMax_combo());
            scoreVO[i].setPositionInList(i);
            scoreVO[i].setPp(scoreDTO.get(i).getPp());
            if(scoreDTO.get(i).getPassed()) {
                scoreVO[i].setRank(scoreDTO.get(i).getRank());
            }else {
                scoreVO[i].setRank("F");
            }
            scoreVO[i].setAvatarUrl(scoreDTO.get(i).getUser().getAvatar_url());
            scoreVO[i].setUser_name(scoreDTO.get(i).getUser().getUsername());
            scoreVO[i].setStatistics(scoreDTO.get(i).getStatistics());
            scoreVO[i].setMode(String.valueOf(scoreDTO.get(i).getRuleset_id()));
            scoreVO[i].setBeatmap(TransformerUtil.beatmapTransform(scoreDTO.get(i).getBeatmap(),scoreDTO.get(i).getBeatmapset()));
        }
        return scoreVO;
    }
    public static List<ScoreVO> scoreTransformForList(List<ScoreLazerDTO> scoreDTO)
    {
        List<ScoreVO> scoreVO=new ArrayList<>();
        int index=0;
        for (ScoreLazerDTO scoreLazerDTO : scoreDTO) {
            ScoreVO temp = new ScoreVO();
            temp.setScore(scoreLazerDTO.getClassic_total_score());
            temp.setAccuracy(scoreLazerDTO.getAccuracy());
            temp.setMods(scoreLazerDTO.getMods().stream()
                    .map(Mod::getAcronym)
                    .toArray(String[]::new));
            temp.setModJSON(scoreLazerDTO.getMods());
            temp.setCreate_at(scoreLazerDTO.getEnded_at());
            temp.setMaxCombo(scoreLazerDTO.getMax_combo());
            temp.setPositionInList(index);
            temp.setPp(scoreLazerDTO.getPp());
            if(scoreLazerDTO.getPassed()) {
                temp.setRank(scoreLazerDTO.getRank());
            }
            else {
                temp.setRank("F");
            }
            //do not download avatar here
            temp.setAvatarUrl(scoreLazerDTO.getUser().getAvatar_url());
            temp.setUser_name(scoreLazerDTO.getUser().getUsername());
            temp.setIsLazer(scoreLazerDTO.getLegacy_total_score() == 0);
            temp.setStatistics(scoreLazerDTO.getStatistics());
            temp.setBeatmap(TransformerUtil.beatmapTransform(scoreLazerDTO.getBeatmap(), scoreLazerDTO.getBeatmapset()));
            temp.setMode(String.valueOf(scoreLazerDTO.getRuleset_id()));
            temp.setIsPerfectCombo(scoreLazerDTO.getIs_perfect_combo());
            scoreVO.add(temp);
            index++;
        }
        return scoreVO;
    }
    public static List<ScoreSequence> scoreSequenceListTransform(List<ScoreLazerDTO> scoreLazerDTOS)
    {
        List<ScoreSequence> scoreSequences=new ArrayList<>();
        for (int i=0;i<scoreLazerDTOS.size();i++) {
            ScoreSequence temp = new ScoreSequence();
            temp.setScore(scoreLazerDTOS.get(i).getClassic_total_score());
            temp.setAccuracy(scoreLazerDTOS.get(i).getAccuracy());
            temp.setModList(scoreLazerDTOS.get(i).getMods());
            temp.setAchievedTime(scoreLazerDTOS.get(i).getEnded_at().split("T")[0]);
            temp.setMaxCombo(scoreLazerDTOS.get(i).getMax_combo());
            temp.setPositionInList(i);
            temp.setPp(scoreLazerDTOS.get(i).getPp());
            if(scoreLazerDTOS.get(i).getPassed()) {
                temp.setRank(scoreLazerDTOS.get(i).getRank());
            }
            else {
                temp.setRank("F");
            }
            //do not download avatar here
            temp.setPlayerName(scoreLazerDTOS.get(i).getUser().getUsername());
            temp.setIsLazer(scoreLazerDTOS.get(i).getLegacy_total_score() == 0);
            temp.setStatistics(scoreLazerDTOS.get(i).getStatistics());
            temp.setBeatmap(TransformerUtil.beatmapTransform(scoreLazerDTOS.get(i).getBeatmap(), scoreLazerDTOS.get(i).getBeatmapset()));
            temp.setRulesetId(scoreLazerDTOS.get(i).getRuleset_id());
            temp.setIsPerfectCombo(scoreLazerDTOS.get(i).getIs_perfect_combo());
            temp.setDifferenceBetweenNextScore((int) Math.round(scoreLazerDTOS.size()>i+1?(scoreLazerDTOS.get(i).getPp()-scoreLazerDTOS.get(i+1).getPp()):0));
            scoreSequences.add(temp);
        }
        return scoreSequences;
    }


    public static BeatmapVO beatmapTransform(BeatmapDTO beatmapDTO){
        BeatmapVO beatmapVO=new BeatmapVO();
        beatmapVO.setAccuracy(beatmapDTO.getAccuracy()); //od
        beatmapVO.setAr(beatmapDTO.getAr()); //ar
        beatmapVO.setCs(beatmapDTO.getCs());  //cs
        beatmapVO.setDrain(beatmapDTO.getDrain()); //hp
        beatmapVO.setDifficult_rating(beatmapDTO.getDifficulty_rating());  //star rating
        beatmapVO.setBpm(beatmapDTO.getBpm());
        beatmapVO.setHit_length(beatmapDTO.getHit_length());
        beatmapVO.setTotal_length(beatmapDTO.getTotal_length());
        beatmapVO.setVersion(beatmapDTO.getVersion()); //diff name
        beatmapVO.setStatus(beatmapDTO.getStatus());  //ranked or loved something
        beatmapVO.setArtist(beatmapDTO.getBeatmapset().getArtist());  //song creator
        beatmapVO.setTitle(beatmapDTO.getBeatmapset().getTitle());   //song title
        beatmapVO.setCreator(beatmapDTO.getBeatmapset().getCreator());  //map creator
        beatmapVO.setMax_combo(beatmapDTO.getMax_combo());
        beatmapVO.setCoverUrl(beatmapDTO.getBeatmapset().getCovers().getCover2x());
        beatmapVO.setSid(beatmapDTO.getBeatmapset_id());
        beatmapVO.setBeatmapset_id(beatmapDTO.getBeatmapset_id());
        beatmapVO.setBid(beatmapDTO.getId());
        beatmapVO.setMode_int(beatmapDTO.getMode_int());
        return beatmapVO;
    }
    public static BeatmapVO beatmapTransform(BeatmapDTO beatmapDTO, BeatmapsetDTO beatmapsetDTO){
        beatmapDTO.setBeatmapset(beatmapsetDTO);
        BeatmapVO beatmapVO=new BeatmapVO();
        beatmapVO.setAccuracy(beatmapDTO.getAccuracy()); //od
        beatmapVO.setAr(beatmapDTO.getAr()); //ar
        beatmapVO.setCs(beatmapDTO.getCs());  //cs
        beatmapVO.setDrain(beatmapDTO.getDrain()); //hp
        beatmapVO.setDifficult_rating(beatmapDTO.getDifficulty_rating());  //star rating
        beatmapVO.setBpm(beatmapDTO.getBpm());
        beatmapVO.setHit_length(beatmapDTO.getHit_length());
        beatmapVO.setTotal_length(beatmapDTO.getTotal_length());
        beatmapVO.setVersion(beatmapDTO.getVersion()); //diff name
        beatmapVO.setStatus(beatmapDTO.getStatus());  //ranked or loved something
        beatmapVO.setArtist(beatmapDTO.getBeatmapset().getArtist());  //song creator
        beatmapVO.setTitle(beatmapDTO.getBeatmapset().getTitle());   //song title
        beatmapVO.setCreator(beatmapDTO.getBeatmapset().getCreator());  //map creator
        beatmapVO.setMax_combo(beatmapDTO.getMax_combo());
        beatmapVO.setCoverUrl(beatmapDTO.getBeatmapset().getCovers().getCover2x());
        beatmapVO.setSid(beatmapDTO.getBeatmapset_id());
        beatmapVO.setBeatmapset_id(beatmapDTO.getBeatmapset_id());
        beatmapVO.setBid(beatmapDTO.getId());
        return beatmapVO;
    }
    public static BeatmapSetVO beatmapSetTransform(BeatmapsetDTO beatmapetDTO) {
        BeatmapSetVO beatmapsetVO=new BeatmapSetVO();
        beatmapsetVO.setArtist(beatmapetDTO.getArtist());
        beatmapsetVO.setTitle(beatmapetDTO.getTitle());
        beatmapsetVO.setCreator(beatmapetDTO.getCreator());
        beatmapsetVO.setSid(beatmapetDTO.getId());
        beatmapsetVO.setStatus(beatmapetDTO.getStatus());
        return beatmapsetVO;
    }
    public static HitScoreVO HitScoreTransform(HitScore hitScore) {
        HitScoreVO hitScoreVO=new HitScoreVO();
        hitScoreVO.setBeatmap_id(hitScore.getBeatmap_id());
        hitScoreVO.setMods(hitScore.getMods());
        hitScoreVO.setPp(hitScore.getPp());
        hitScoreVO.setRank(hitScore.getRank());
        hitScoreVO.setScore(hitScore.getScore());
        hitScoreVO.setAchievedTime(CommonTool.tranfromDate(
                    Integer.valueOf(hitScore.getScore_time().substring(0,4)),
                Integer.valueOf(hitScore.getScore_time().substring(5,7))-1,
                    Integer.valueOf(hitScore.getScore_time().substring(8,10)),
                    Integer.valueOf(hitScore.getScore_time().substring(11,13)),
                    Integer.valueOf(hitScore.getScore_time().substring(14,16)),0));
        return hitScoreVO;
    }
    public static List<HitScoreVO> HitScoreTransform(List<HitScore> hitScores) {
        List<HitScoreVO> hitScoreVOs=new ArrayList<>();
        for(int i=0;i<hitScores.size();i++)
        {
            hitScoreVOs.add(new HitScoreVO());
            hitScoreVOs.get(i).setBeatmap_id(hitScores.get(i).getBeatmap_id());
            hitScoreVOs.get(i).setMods(hitScores.get(i).getMods());
            hitScoreVOs.get(i).setPp(hitScores.get(i).getPp());
            hitScoreVOs.get(i).setRank(hitScores.get(i).getRank());
            hitScoreVOs.get(i).setScore(hitScores.get(i).getScore());
            hitScoreVOs.get(i).setScoreTimeJSON(hitScores.get(i).getScore_time());
            hitScoreVOs.get(i).setAchievedTime(CommonTool.tranfromDate(
                    Integer.valueOf(hitScores.get(i).getScore_time().substring(0, 4)),
                    Integer.valueOf(hitScores.get(i).getScore_time().substring(5, 7))-1,
                    Integer.valueOf(hitScores.get(i).getScore_time().substring(8, 10)),
                    Integer.valueOf(hitScores.get(i).getScore_time().substring(11, 13)),
                    Integer.valueOf(hitScores.get(i).getScore_time().substring(14, 16)),0));
        }
        return hitScoreVOs;
    }

    public static InputStream transformDocumentToStream(Document document) throws Exception {
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(outputStream);
        transformer.transform(source, result);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
    public static ScoreLazerVO transformScoreLazerToScoreLazerVO(ScoreLazerDTO scoreLazer)
    {
        ScoreLazerVO score=new ScoreLazerVO();
        score.setScore(scoreLazer.getClassic_total_score());
        score.setAccuracy(scoreLazer.getAccuracy());
        score.setMods(scoreLazer.getModsArray());
        score.setCreate_at(scoreLazer.getEnded_at());
        score.setMaxCombo(scoreLazer.getMax_combo());
        score.setPassed(scoreLazer.getPassed());
        score.setPp(scoreLazer.getPp());
        score.setStatistics(scoreLazer.getStatistics());
        if(scoreLazer.getPassed()) {
            score.setRank(scoreLazer.getRank());
        }
        else {
            score.setRank("F");
        }
        score.setAvatarUrl(scoreLazer.getUser().getAvatar_url());
        return score;
    }
    public static ScoreVO transformScoreLazerToScoreVO(ScoreLazerDTO scoreLazer)
    {
        ScoreVO score=new ScoreVO();
        score.setScore(scoreLazer.getClassic_total_score());
        score.setAccuracy(scoreLazer.getAccuracy());
        score.setMods(scoreLazer.getModsArray());
        score.setModJSON(scoreLazer.getMods());
        score.setCreate_at(scoreLazer.getEnded_at());
        score.setMaxCombo(scoreLazer.getMax_combo());
        score.setPp(scoreLazer.getPp());
        score.setStatistics(scoreLazer.getStatistics());
        if (scoreLazer.getPassed()) {
            score.setRank(scoreLazer.getRank());
        }
        else {
            score.setRank("F");
        }
        score.setAvatarUrl(AssertDownloadUtil.avatarAbsolutePath(scoreLazer.getUser(),false));
        score.setIsLazer(scoreLazer.getLegacy_total_score() == 0);
        score.setUser_name(scoreLazer.getUser().getUsername());
        score.setMode(String.valueOf(scoreLazer.getRuleset_id()));
        score.setIsPerfectCombo(scoreLazer.getIs_perfect_combo());
        return score;
    }
    public static BeatmapCompactPO reverseBeatmapPO(BeatmapDTO beatmapDTO)
    {
        return new BeatmapCompactPO(beatmapDTO.getId(),
                beatmapDTO.getBeatmapset_id(),
                beatmapDTO.getMax_combo(),
                beatmapDTO.getMode_int());
    }
    public static BeatmapCompactPO reverseBeatmapPO(BeatmapVO beatmapVO)
    {
        return new BeatmapCompactPO(beatmapVO.getBid(),
                beatmapVO.getBeatmapset_id(),
                beatmapVO.getMax_combo(),
                beatmapVO.getMode_int());
    }
}
