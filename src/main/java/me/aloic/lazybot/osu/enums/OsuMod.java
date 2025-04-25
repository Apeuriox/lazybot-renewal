package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.spring.osu.model.LazerMod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum OsuMod
{
    None(0, "NM"),
    NoFail(1, "NF"),
    Easy(1 << 1, "EZ"),
    TouchDevice(1 << 2, "TD"),
    Hidden(1 << 3, "HD"),
    HardRock(1 << 4, "HR"),
    SuddenDeath(1 << 5, "SD"),
    DoubleTime(1 << 6, "DT"),
    Relax(1 << 7, "RX"),
    HalfTime(1 << 8, "HT"),
    Nightcore((1 << 9) + (DoubleTime.value), "NC"),
    Flashlight(1 << 10, "FL"),
    Autoplay(1 << 11, "AT"),
    SpunOut(1 << 12, "SO"),
    Autopilot(1 << 13, "AP"),
    Perfect(1 << 14, "PF"),
    Key4(1 << 15, "4K"),
    Key5(1 << 16, "5K"),
    Key6(1 << 17, "6K"),
    Key7(1 << 18, "7K"),
    Key8(1 << 19, "8K"),
    FadeIn(1 << 20, "FI"),
    Random(1 << 21, "RD"),
    Cinema(1 << 22, "CM"),
    TargetPractice(1 << 23, "TP"),
    Key9(1 << 24, "9K"),
    KeyCoop(1 << 25, "CP"),
    Key1(1 << 26, "1K"),
    Key3(1 << 27, "3K"),
    Key2(1 << 28, "2K"),
    ScoreV2(1 << 29, "V2"),
    Mirror(1 << 30, "MR"),
    KeyMod(521109504, "KEY"),
    FreeMod(522171579, "FM"),
    ScoreIncreaseMods(1049688, "IM"),
    Other(-1, "OTHER");

    @Getter
    private final int value;
    private final String acronym;


    public String getModEnum() {
        return acronym;
    }

    public static List<OsuMod> getAllMod(int value) {
        return Arrays.stream(values())
                .filter(mod -> (mod.getValue() & value) == mod.getValue())
                .collect(Collectors.toList());
    }

    public static OsuMod getModEnum(int value) {
        return Arrays.stream(values())
                .filter(mod -> (mod.getValue() & value) == mod.getValue())
                .findFirst()
                .orElse(null);
    }

    public static OsuMod getModEnum(String acronym) {
        String modAcronym = acronym.trim().toUpperCase();
        switch (modAcronym) {
            case "NM":
                return None;
            case "NF":
                return NoFail;
            case "EZ":
                return Easy;
            case "TD":
                return TouchDevice;
            case "HD":
                return Hidden;
            case "HR":
                return HardRock;
            case "SD":
                return SuddenDeath;
            case "DT":
                return DoubleTime;
            case "RX":
                return Relax;
            case "HT":
                return HalfTime;
            case "NC":
                return Nightcore;
            case "FL":
                return Flashlight;
            case "AT":
                return Autoplay;
            case "SO":
                return SpunOut;
            case "AP":
                return Autopilot;
            case "PF":
                return Perfect;
            case "4K":
                return Key4;
            case "5K":
                return Key5;
            case "6K":
                return Key6;
            case "7K":
                return Key7;
            case "8K":
                return Key8;
            case "FI":
                return FadeIn;
            case "RD":
                return Random;
            case "CM":
                return Cinema;
            case "TP":
                return TargetPractice;
            case "9K":
                return Key9;
            case "CP":
                return KeyCoop;
            case "1K":
                return Key1;
            case "3K":
                return Key3;
            case "2K":
                return Key2;
            case "V2":
                return ScoreV2;
            case "MR":
                return Mirror;
            case "KEY":
                return KeyMod;
            case "FM":
                return FreeMod;
            case "IM":
                return ScoreIncreaseMods;
            default:
                return Other;
        }
    }
    public static org.spring.osu.model.OsuMod getModEnumJNI(String acronym) {
        String modAcronym = acronym.trim().toUpperCase();
        switch (modAcronym) {
            case "NM":
                return org.spring.osu.model.OsuMod.None;
            case "NF":
                return org.spring.osu.model.OsuMod.NoFail;
            case "EZ":
                return org.spring.osu.model.OsuMod.Easy;
            case "TD":
                return org.spring.osu.model.OsuMod.TouchDevice;
            case "HD":
                return org.spring.osu.model.OsuMod.Hidden;
            case "HR":
                return org.spring.osu.model.OsuMod.HardRock;
            case "SD":
                return org.spring.osu.model.OsuMod.SuddenDeath;
            case "DT":
                return org.spring.osu.model.OsuMod.DoubleTime;
            case "RX":
                return org.spring.osu.model.OsuMod.Relax;
            case "HT":
                return org.spring.osu.model.OsuMod.HalfTime;
            case "NC":
                return org.spring.osu.model.OsuMod.Nightcore;
            case "FL":
                return org.spring.osu.model.OsuMod.Flashlight;
            case "AT":
                return org.spring.osu.model.OsuMod.Autoplay;
            case "SO":
                return org.spring.osu.model.OsuMod.SpunOut;
            case "AP":
                return org.spring.osu.model.OsuMod.Autopilot;
            case "PF":
                return org.spring.osu.model.OsuMod.Perfect;
            case "4K":
                return org.spring.osu.model.OsuMod.Key4;
            case "5K":
                return org.spring.osu.model.OsuMod.Key5;
            case "6K":
                return org.spring.osu.model.OsuMod.Key6;
            case "7K":
                return org.spring.osu.model.OsuMod.Key7;
            case "8K":
                return org.spring.osu.model.OsuMod.Key8;
            case "FI":
                return org.spring.osu.model.OsuMod.FadeIn;
            case "RD":
                return org.spring.osu.model.OsuMod.Random;
            case "CM":
                return org.spring.osu.model.OsuMod.Cinema;
            case "TP":
                return org.spring.osu.model.OsuMod.TargetPractice;
            case "9K":
                return org.spring.osu.model.OsuMod.Key9;
            case "CP":
                return org.spring.osu.model.OsuMod.KeyCoop;
            case "1K":
                return org.spring.osu.model.OsuMod.Key1;
            case "3K":
                return org.spring.osu.model.OsuMod.Key3;
            case "2K":
                return org.spring.osu.model.OsuMod.Key2;
            case "V2":
                return org.spring.osu.model.OsuMod.ScoreV2;
            case "MR":
                return org.spring.osu.model.OsuMod.Mirror;
            case "KEY":
                return org.spring.osu.model.OsuMod.KeyMod;
            case "FM":
                return org.spring.osu.model.OsuMod.FreeMod;
            case "IM":
                return org.spring.osu.model.OsuMod.ScoreIncreaseMods;
            default:
                return org.spring.osu.model.OsuMod.Other;
        }
    }
    public static LazerMod getModEnumLazer(String acronym) {
        String modAcronym = acronym.trim().toUpperCase();
        switch (modAcronym) {
            case "NF":
                return new LazerMod.NoFail();
            case "EZ":
                return new LazerMod.Easy();
            case "TD":
                return new LazerMod.TouchDevice();
            case "HD":
                return new LazerMod.Hidden();
            case "HR":
                return new LazerMod.HardRock();
            case "SD":
                return new LazerMod.SuddenDeath();
            case "DT":
                return new LazerMod.DoubleTime();
            case "RX":
                return new LazerMod.Relax();
            case "HT":
                return new LazerMod.HalfTime();
            case "NC":
                return new LazerMod.Nightcore();
            case "FL":
                return new LazerMod.Flashlight();
            case "AT":
                return new LazerMod.Autoplay();
            case "SO":
                return new LazerMod.SpunOut();
            case "AP":
                return new LazerMod.Autopilot();
            case "PF":
                return new LazerMod.Perfect();
            case "4K":
                return new LazerMod.Key4();
            case "5K":
                return new LazerMod.Key5();
            case "6K":
                return new LazerMod.Key6();
            case "7K":
                return new LazerMod.Key7();
            case "8K":
                return new LazerMod.Key8();
            case "FI":
                return new LazerMod.FadeIn();
            case "RD":
                return new LazerMod.Random();
            case "CM":
                return new LazerMod.Cinema();
            case "TP":
                return new LazerMod.TargetPractice();
            case "9K":
                return new LazerMod.Key9();
            case "1K":
                return new LazerMod.Key1();
            case "3K":
                return new LazerMod.Key3();
            case "2K":
                return new LazerMod.Key2();
            case "V2":
                return new LazerMod.ScoreV2();
            case "MR":
                return new LazerMod.Mirror();
        }
        throw new LazybotRuntimeException("No such mods");
    }


    public static int getModValue(String acronym) {
        return getModEnum(acronym).getValue();
    }

    public static List<OsuMod> getAllMod(List<String> acronyms) {
        return acronyms.stream()
                .map(OsuMod::getModEnum)
                .filter(mod -> mod != Other)
                .collect(Collectors.toList());
    }
    public static List<String> getAllModAcronym(String acronyms) {
        if (acronyms.trim().isEmpty()) return Collections.emptyList();
        String modsStr = acronyms.toUpperCase(Locale.getDefault()).replaceAll("\\s+", "");
        if (modsStr.length() % 2 != 0) throw new LazybotRuntimeException("无效mod组合: " + acronyms);
        List<String> modStrList = Arrays.stream(modsStr.split("(?<=\\G.{2})"))
                .collect(Collectors.toList());
        return modStrList;
    }
    public static List<org.spring.osu.model.OsuMod> getAllModJNI(List<String> acronyms) {
        return acronyms.stream()
                .map(OsuMod::getModEnumJNI)
                .filter(mod -> mod != org.spring.osu.model.OsuMod.Other)
                .collect(Collectors.toList());
    }

    public static int getAllModValue(List<String> acronyms) {
        return getAllMod(acronyms).stream()
                .mapToInt(OsuMod::getValue)
                .sum();
    }

    public static List<OsuMod> getAllMod(String acronyms) {
        if (acronyms.trim().isEmpty()) return Collections.emptyList();
        String modsStr = acronyms.toUpperCase(Locale.getDefault()).replaceAll("\\s+", "");
        if (modsStr.length() % 2 != 0) throw new LazybotRuntimeException("Invalid mods combination input: " + acronyms);
        List<String> modStrList = Arrays.stream(modsStr.split("(?<=\\G.{2})"))
                .collect(Collectors.toList());
        return getAllMod(modStrList);
    }
    public static List<org.spring.osu.model.OsuMod> getAllModJNI(String acronyms) {
        if (acronyms.trim().isEmpty()) return Collections.emptyList();
        String modsStr = acronyms.toUpperCase(Locale.getDefault()).replaceAll("\\s+", "");
        if (modsStr.length() % 2 != 0) throw new LazybotRuntimeException("Invalid mods combination input: " + acronyms);
        List<String> modStrList = Arrays.stream(modsStr.split("(?<=\\G.{2})"))
                .collect(Collectors.toList());
        return getAllModJNI(modStrList);
    }

    public static int getAllModValue(String acronyms) {
        return getAllMod(acronyms).stream()
                .mapToInt(OsuMod::getValue)
                .sum();
    }

    public static boolean hasRatingChange(int value) {
        return (changeRatingValue & value) != 0;
    }

    public static boolean hasRatingChange(List<OsuMod> acronyms) {
        return acronyms.stream()
                .anyMatch(acronym -> (acronym.getValue() & changeRatingValue) != 0);
    }

    public static float changeAR(float value, boolean dt, boolean hr, boolean ht, boolean ez) {
        float ar = value;
        if (hr) {
            ar = Math.min(10f, value * 1.4f);
        } else if (ez) {
            ar = Math.max(0f, value * 0.5f);
        }

        if (dt || ht) {
            float ms = 0f;
            if (ar > 11f) {
                ms = 300f;
            } else if (ar > 5f) {
                ms = 1200 - (150 * (ar - 5));
            } else if (ar > 0f) {
                ms = 1800 - (120 * ar);
            } else {
                ms = 1800f;
            }

            ms /= dt ? 1.5f : 0.75f;

            ar = ms < 300 ? 11f : (ms < 1200 ? 5 + (1200 - ms) / 150f : (ms < 2400 ? (1800 - ms) / 120f : -5f));
        }
        return ar;
    }

    public static float changeOD(float value, boolean dt, boolean hr, boolean ht, boolean ez) {
        float od = value;
        if (hr) {
            od = Math.min(10f, value * 1.4f);
        } else if (ez) {
            od = Math.max(0f, value * 0.5f);
        }

        if (dt || ht) {
            float ms = od < 11 ? 80 - od * 6 : 14f;
            ms /= dt ? 1.5f : 0.75f;
            od = ms < 14 ? (80 - ms) / 6 : 11f;
        }
        return od;
    }

    public static float changeCS(float value, boolean isHR) {
        return isHR ? Math.min(10f, value * 1.3f) : Math.max(0f, value / 2);
    }

    public static float changeHP(float value, boolean isHR) {
        return isHR ? Math.min(10f, value * 1.4f) : Math.max(0f, value / 2);
    }

    public static float changeBPM(float value, boolean isDT) {
        return isDT ? value * 1.5f : value * 0.75f;
    }

    private static final int changeRatingValue = Easy.value | HalfTime.value | TouchDevice.value | HardRock.value | DoubleTime.value | Nightcore.value | Flashlight.value;
    private static final int changeSpeedValue = DoubleTime.value | Nightcore.value | HalfTime.value;
//    private static final Pattern emptyReg = Pattern.compile("\\s+");
//    private static final Pattern splitReg = Pattern.compile("(?<=(\\w{2})+)(?=(\\w{2})+$)");


    public static int plus(int value, OsuMod mod) {
        return value | mod.getValue();
    }
}


