package me.aloic.lazybot.osu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.aloic.lazybot.exception.LazybotRuntimeException;
import org.spring.osu.model.LazerMod;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum OsuMod
{
    None(0, "NM", List.of("NoMod")),
    NoFail(1, "NF", List.of("NoFail")),
    Easy(1 << 1, "EZ", List.of("E")),
    TouchDevice(1 << 2, "TD",List.of("Touch")),
    Hidden(1 << 3, "HD", List.of("Hide","HiddenIsFun","HIS")),
    HardRock(1 << 4, "HR", List.of("Hard")),
    SuddenDeath(1 << 5, "SD", List.of("Sudden","Death")),
    DoubleTime(1 << 6, "DT", List.of("Double")),
    Relax(1 << 7, "RX", List.of("RL")),
    HalfTime(1 << 8, "HT", List.of("HALF")),
    Nightcore((1 << 9) + (DoubleTime.value), "NC", List.of("Night")),
    Flashlight(1 << 10, "FL", List.of("Flash")),
    Autoplay(1 << 11, "AT", List.of("Auto")),
    SpunOut(1 << 12, "SO",List.of("SpinOut","NoSpin")),
    Autopilot(1 << 13, "AP", List.of("NoClick")),
    Perfect(1 << 14, "PF",List.of("Perfectly")),
    Key4(1 << 15, "Keys", List.of("4Keys", "4Key", "K4", "4K")),
    Key5(1 << 16, "Keys", List.of("5Keys", "5Key", "K5", "5K")),
    Key6(1 << 17, "Keys", List.of("6Keys", "6Key", "K6", "6K")),
    Key7(1 << 18, "Keys", List.of("7Keys", "7Key", "K7", "7K")),
    Key8(1 << 19, "Keys", List.of("8Keys", "8Key", "K8", "8K")),
    FadeIn(1 << 20, "FI", List.of("Fade")),
    Random(1 << 21, "RD", List.of("Rand")),
    Cinema(1 << 22, "CN", List.of("CM")),
    TargetPractice(1 << 23, "TP", List.of("Target", "Practice")),
    Key9(1 << 24, "Keys", List.of("9Keys", "9Key", "K9", "K9")),
    KeyCoop(1 << 25, "CP", List.of("KC", "Coop")),
    Key1(1 << 26, "Keys", List.of("1Keys", "1Key", "K1", "1K")),
    Key3(1 << 27, "Keys", List.of("3Keys", "3Key", "K3", "3K")),
    Key2(1 << 28, "Keys", List.of("2Keys", "2Key", "K2", "2K")),
    ScoreV2(1 << 29, "SV2", List.of("SV2")),
    Mirror(1 << 30, "MR", List.of("Mir")),
    KeyMod(521109504, "KEY", List.of("KM")),
    FreeMod(522171579, "FM", List.of("Free","Unlock")),
    ScoreIncreaseMods(1049688, "IM", List.of("SIM")),
    //not existing
    Other(-1, "OTHER", List.of("Others")),

    Muted(-1, "MU", List.of("Mute")),
    Blinds(-1, "BL", List.of("BoyLove")),
    StrictTracking(-1, "ST", List.of("Track", "Strict")),
    AccuracyChallenge(-1, "AC", List.of("Accuracy", "Challenge")),
    DifficultyAdjust(-1, "DA", List.of("Adjust")),
    SingleTap(-1, "SG", List.of("SteinsGate", "Steins", "Single")),
    Daycore(-1, "DC", List.of("Day")),
    Cover(-1, "CO", List.of("Covers")),
    Classic(-1, "CL", List.of("Stable")),
    Alternate(-1, "AL", List.of("Alt")),
    Swap(-1, "SW", List.of("Exchange")),
    Invert(-1, "IN", List.of("Hold")),
    ConstantSpeed(-1, "CS", List.of("Speed", "Constant")),
    HoldOff(-1, "HO", List.of("Off")),
    Transform(-1, "TR", List.of("Trans","TF")),
    Wiggle(-1, "WG", List.of("Wig")),
    SpinIn(-1, "SI", List.of("Spin")),
    Grow(-1, "GR", List.of("Grows")),
    Deflate(-1, "DF", List.of("Flate")),
    WindUp(-1, "WU", List.of("Up")),
    WindDown(-1, "WD", List.of("Down")),
    Traceable(-1, "TC", List.of("Trace")),
    BarrelRoll(-1, "BR", List.of("Roll")),
    ApproachDifferent(-1, "AD", List.of("Different")),
    FloatingFruits(-1, "FF", List.of("Fruits")),
    NoScope(-1, "NS", List.of("Scope")),
    Magnetised(-1, "MG", List.of("AimAssist","Magnet")),
    Repel(-1, "RP", List.of("Repels")),
    AdaptiveSpeed(-1, "AS", List.of("Adaptive")),
    FreezeFrame(-1, "FF", List.of("Freeze")),
    Bubbles(-1, "BU", List.of("Bubble")),
    Synesthesia(-1, "SY", List.of("Syn")),
    Depth(-1, "DP", List.of("Deep")),
    Bloom(-1, "BM", List.of("Boom")),
    NoRelease(-1, "NR", List.of("Release"));


    @Getter
    private final int value;
    private final String acronym;
    private final List<String> alias;
    public static final Map<String, OsuMod> lookupMap;

    static {
        Map<String, OsuMod> map = new HashMap<>();
        for (OsuMod e : OsuMod.values()) {
            map.putIfAbsent(e.acronym.toLowerCase(), e);
            for (String a : e.alias) {
                map.put(a.toLowerCase(), e);
            }
        }
        lookupMap = Collections.unmodifiableMap(map);
    }



    OsuMod(int value, String acronym, List<String> alias) {
        this.value = value;
        this.acronym = acronym;
        List<String> allAliases = new ArrayList<>(alias);
        allAliases.add(this.name());
        this.alias = Collections.unmodifiableList(allAliases);
    }
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
        return switch (modAcronym)
        {
            case "NM" -> None;
            case "NF" -> NoFail;
            case "EZ" -> Easy;
            case "TD" -> TouchDevice;
            case "HD" -> Hidden;
            case "HR" -> HardRock;
            case "SD" -> SuddenDeath;
            case "DT" -> DoubleTime;
            case "RX" -> Relax;
            case "HT" -> HalfTime;
            case "NC" -> Nightcore;
            case "FL" -> Flashlight;
            case "AT" -> Autoplay;
            case "SO" -> SpunOut;
            case "AP" -> Autopilot;
            case "PF" -> Perfect;
            case "4K" -> Key4;
            case "5K" -> Key5;
            case "6K" -> Key6;
            case "7K" -> Key7;
            case "8K" -> Key8;
            case "FI" -> FadeIn;
            case "RD" -> Random;
            case "CM" -> Cinema;
            case "TP" -> TargetPractice;
            case "9K" -> Key9;
            case "CP" -> KeyCoop;
            case "1K" -> Key1;
            case "3K" -> Key3;
            case "2K" -> Key2;
            case "V2" -> ScoreV2;
            case "MR" -> Mirror;
            case "KEY" -> KeyMod;
            case "FM" -> FreeMod;
            case "IM" -> ScoreIncreaseMods;
            default -> Other;
        };
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

    public static String findAcronym(String input) {
        if (input == null) return null;
        OsuMod match = lookupMap.get(input.toLowerCase().replaceAll("\\s+", ""));
        return match != null ? match.getAcronym() : null;
    }
}


