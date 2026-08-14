@import me.aloic.lazybot.osu.dao.entity.vo.PlayerInfoVO
@import me.aloic.lazybot.util.FormatHelper
@import me.aloic.lazybot.graphics.mapping.SVGElementHelper
@import me.aloic.lazybot.util.ColorUtils

@param PlayerInfoVO  player
@param Integer hue
@param Double saturationFactor

<svg width="633" height="1080" viewBox="0 0 633 1080" fill="none" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns="http://www.w3.org/2000/svg">
<path d="M633 0H0V1080H633V0Z" fill="#202020"/>
<path d="M90 0H0V1080H90V0Z" fill="hsl(${ColorUtils.circularHueSubtract(hue,-9)}, ${(int) Math.round(98 * saturationFactor)}%, 49%)"/>
<text transform="matrix(0 1 -1 0 68 16)" fill="#EEEEEE" font-family="Marathon Shapiro" font-size="50" font-weight="30" letter-spacing="0em"><tspan x="0" y="49">PLAYER STATISTICS</tspan></text>
<text transform="matrix(0 1 -1 0 81.8999 20)" fill="#EEEEEE" font-family="KH Interference" font-size="16" letter-spacing="0em"><tspan x="0" y="15.1">Request date</tspan></text>
<text transform="matrix(0 1 -1 0 81.8999 176)" fill="#EEEEEE" font-family="KH Interference" font-size="16" letter-spacing="0em"><tspan x="0" y="15.1">${SVGElementHelper.dateNowMarathon()}</tspan></text>
    <image opacity="0.9"
           xlink:href="${player.getAvatarUrl()}"
           x="90" width="543" height="539" preserveAspectRatio="xMidYMid slice" id="avatar" />
<g class="mark-between">
    <path d="M72.8 158H69.8V161H72.8V158Z" fill="#EEEEEE"/>
    <path d="M66.3501 154.646L75.9202 164.211" stroke="#EEEEEE"/>
    <path d="M66.6501 164.211L76.2101 154.646" stroke="#EEEEEE"/>
</g>

<g class="left-deco-and-text">
    <path d="M56.5 869.5H32.5V909.5H56.5V869.5Z" stroke="#e9ebea"/>
    <path d="M56.5 869.5H32.5V894.5H56.5V869.5Z" stroke="#e9ebea"/>
    <path d="M44.5 929.5H33.5V940.5H44.5V929.5Z" stroke="#e9ebea"/>
    <text transform="matrix(0 1 -1 0 51.0999 878)" fill="#e9ebea" font-family="Marathon Shapiro" font-size="10" font-weight="30" letter-spacing="0em"><tspan x="0" y="9.9">R</tspan></text>
    <text transform="matrix(0 1 -1 0 51.0999 898)" fill="#e9ebea" font-family="Marathon Shapiro" font-size="10" font-weight="30" letter-spacing="0em"><tspan x="0" y="9.9">0</tspan></text>
    <text transform="matrix(0 1 -1 0 57.3999 949)" fill="#e9ebea" font-family="KH Interference" font-size="6" letter-spacing="0em"><tspan x="0" y="5.6">GENERATED IMAGE BY LAZYBOT</tspan></text>
    <text transform="matrix(0 1 -1 0 39.3999 949)" fill="#e9ebea" font-family="KH Interference" font-size="6" letter-spacing="0em"><tspan x="0" y="5.6">RESULT MAY BE DELAYED OR CACHED</tspan></text>
    <text transform="matrix(0 1 -1 0 48.3999 949)" fill="#e9ebea" font-family="KH Interference" font-size="6" letter-spacing="0em"><tspan x="0" y="5.6">LAZYBOT 1.3.13/20260814</tspan></text>
    <path d="M57 917H45V929H57V917Z" fill="#e9ebea"/>
    <path d="M42 932H36V938H42V932Z" fill="#e9ebea"/>
</g>


<path d="M633 539H90V845H633V539Z" fill="hsl(${hue}, ${(int) Math.round(63 * saturationFactor)}%, 78%)"/>
    <g id="info-bg-decos">
        <path opacity="0.05" d="M433 732H90V845H433V732Z" fill="#111111"/>
        <path opacity="0.05" d="M433 716H596V845H433V716Z" fill="#111111"/>
        <path opacity="0.05" d="M290 587H453V716H290V587Z" fill="#111111"/>
        <path opacity="0.05" d="M90 639H290V685H90V639Z" fill="#111111"/>
        <path opacity="0.05" d="M141 539H238V639H141V539Z" fill="#111111"/>
        <path opacity="0.05" d="M453 560H633V622H453V560Z" fill="#111111"/>
    </g>


<text fill="${saturationFactor<0.1?"#e9ebea":"#111111"}" font-family="KH Interference" font-size="16" letter-spacing="0em"><tspan x="141" y="645.2">PLAYERNAME</tspan></text>
<text fill="${saturationFactor<0.1?"#e9ebea":"#111111"}" font-family="Marathon Shapiro" font-size="${player.getPlayerName().length()>6?Math.max(2,(60 - (player.getPlayerName().length() - 6)*2)):60}" font-weight="30" letter-spacing="0em"><tspan x="141" y="627.8">${player.getPlayerName()}</tspan></text>
<text fill="${saturationFactor<0.1?"#e9ebea":"#111111"}" font-family="Marathon Shapiro" font-size="60" font-weight="30" letter-spacing="0em"><tspan x="138" y="773.8">${FormatHelper.transformNumber((int) Math.round(player.getPerformancePoint()))}</tspan></text>
<text fill="${saturationFactor<0.1?"#e9ebea":"#111111"}" font-family="KH Interference" font-size="16" letter-spacing="0em"><tspan x="141" y="801.2">PERFORMANCE</tspan></text>

    <g id="info-decos">
        <g id="info-decos-stroke">
            <path d="M99 560H118" stroke="#5C4E5F"/>
            <path d="M108.5 570V551" stroke="#5C4E5F"/>/
            <path d="M99 825H118" stroke="#5C4E5F"/>
            <path d="M108.5 835V816" stroke="#5C4E5F"/>
            <path d="M605 560H624" stroke="#5C4E5F"/>
            <path d="M614.5 570V551" stroke="#5C4E5F"/>
            <path d="M605 825H624" stroke="#5C4E5F"/>
            <path d="M614.5 835V816" stroke="#5C4E5F"/>
        </g>
        <g id="info-decos-fill">
            <path d="M610 591H608V593H610V591Z" fill="#5C4E5F"/>
            <path d="M610 602H608V604H610V602Z" fill="#5C4E5F"/>
            <path d="M610 597H608V599H610V597Z" fill="#5C4E5F"/>
            <path d="M610 608H608V610H610V608Z" fill="#5C4E5F"/>
            <path d="M610 614H608V616H610V614Z" fill="#5C4E5F"/>
            <path d="M616 591H614V593H616V591Z" fill="#5C4E5F"/>
            <path d="M616 602H614V604H616V602Z" fill="#5C4E5F"/>
            <path d="M616 597H614V599H616V597Z" fill="#5C4E5F"/>
            <path d="M616 608H614V610H616V608Z" fill="#5C4E5F"/>
            <path d="M616 614H614V616H616V614Z" fill="#5C4E5F"/>
            <path d="M622 591H620V593H622V591Z" fill="#5C4E5F"/>
            <path d="M622 602H620V604H622V602Z" fill="#5C4E5F"/>
            <path d="M622 597H620V599H622V597Z" fill="#5C4E5F"/>
            <path d="M622 608H620V610H622V608Z" fill="#5C4E5F"/>
            <path d="M622 614H620V616H622V614Z" fill="#5C4E5F"/>
            <path d="M610 620H608V622H610V620Z" fill="#5C4E5F"/>
            <path d="M610 631H608V633H610V631Z" fill="#5C4E5F"/>
            <path d="M610 626H608V628H610V626Z" fill="#5C4E5F"/>
            <path d="M610 637H608V639H610V637Z" fill="#5C4E5F"/>
            <path d="M610 643H608V645H610V643Z" fill="#5C4E5F"/>
            <path d="M616 620H614V622H616V620Z" fill="#5C4E5F"/>
            <path d="M616 631H614V633H616V631Z" fill="#5C4E5F"/>
            <path d="M616 626H614V628H616V626Z" fill="#5C4E5F"/>
            <path d="M616 637H614V639H616V637Z" fill="#5C4E5F"/>
            <path d="M616 643H614V645H616V643Z" fill="#5C4E5F"/>
            <path d="M622 620H620V622H622V620Z" fill="#5C4E5F"/>
            <path d="M622 631H620V633H622V631Z" fill="#5C4E5F"/>
            <path d="M622 626H620V628H622V626Z" fill="#5C4E5F"/>
            <path d="M622 637H620V639H622V637Z" fill="#5C4E5F"/>
            <path d="M622 643H620V645H622V643Z" fill="#5C4E5F"/>
        </g>
    </g>

<mask id="mask0_2411_570" style="mask-type:alpha" maskUnits="userSpaceOnUse" x="66" y="691" width="24" height="119">
<path d="M90 691H66V810H90V691Z" fill="#D9D9D9"/>
</mask>
<g mask="url(#mask0_2411_570)" opacity="${saturationFactor < 0.1 ? 1 : 0}">
    <path d="M90 675V684L66 706V697L90 675Z" fill="#D9D9D9"/>
    <path d="M90 694V703L66 725V716L90 694Z" fill="#D9D9D9"/>
    <path d="M90 713V722L66 744V735L90 713Z" fill="#D9D9D9"/>
    <path d="M90 732V741L66 763V754L90 732Z" fill="#D9D9D9"/>
    <path d="M90 751V760L66 782V773L90 751Z" fill="#D9D9D9"/>
    <path d="M90 770V779L66 801V792L90 770Z" fill="#D9D9D9"/>
    <path d="M90 789V798L66 820V811L90 789Z" fill="#D9D9D9"/>
    <path d="M90 808V817L66 839V830L90 808Z" fill="#D9D9D9"/>
</g>

<path d="M633 845H90V1080H633V845Z" fill="hsl(${ColorUtils.circularHueSubtract(hue,1)}, ${(int) Math.round(48 * saturationFactor)}%, 65%)"/>
<path d="M226 1041H90V1080H226V1041Z" fill="hsl(${ColorUtils.circularHueSubtract(hue,1)}, ${(int) Math.round(93 * saturationFactor)}%, 22%)"/>
<path d="M361 1041H226V1080H361V1041Z" fill="hsl(${ColorUtils.circularHueSubtract(hue,-10)}, ${(int) Math.round(77 * saturationFactor)}%, 41%)"/>
<path d="M498 1041H361V1080H498V1041Z" fill="hsl(${ColorUtils.circularHueSubtract(hue,-10)}, ${(int) Math.round(85 * saturationFactor)}%, 54%)"/>
<path d="M633 1041H498V1080H633V1041Z" fill="hsl(${ColorUtils.circularHueSubtract(hue,-10)}, ${(int) Math.round(100 * saturationFactor)}%, 67%)"/>
    <g fill="hsl(${ColorUtils.circularHueSubtract(hue,-11)}, ${(int) Math.round(28 * saturationFactor)}%, 34%)">
        <path d="M197 925H199V927H197V925Z"/>
        <path d="M197 962H199V964H197V962Z"/>
        <path d="M197 1000H199V1002H197V1000Z"/>
        <path d="M309 925H311V927H309V925Z"/>
        <path d="M407 925H409V927H407V925Z"/>
        <path d="M419 925H421V927H419V925Z"/>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="203" y="870.36">${FormatHelper.transformNumber(player.getPlayCount())}</tspan></text>
        <path d="M530 925H532V927H530V925Z"/>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="152" y="870.36">PC</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="148" y="905.36">TTH</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="203" y="905.36">${FormatHelper.transformNumber(player.getTotalHitCount())}</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="157" y="942.36">S</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="203" y="942.36">${FormatHelper.transformNumber(player.getGrades().getS()+player.getGrades().getSh())}</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="157" y="979.36">A</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="203" y="979.36">${FormatHelper.transformNumber(player.getGrades().getA())}</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="157" y="1016.36">X</tspan></text>
        <text font-family="Marathon Shapiro" font-size="12" font-weight="30" letter-spacing="0em"><tspan x="203" y="1016.36">${FormatHelper.transformNumber(player.getGrades().getSs()+player.getGrades().getSsh())}</tspan></text>
    </g>
    <g stroke="hsl(${ColorUtils.circularHueSubtract(hue,-11)}, ${(int) Math.round(28 * saturationFactor)}%, 34%)">
        <path d="M183.5 852.5H141.5V881.5H183.5V852.5Z"/>
        <path d="M183.5 887.5H141.5V916.5H183.5V887.5Z"/>
        <path d="M183.5 923.5H141.5V952.5H183.5V923.5Z"/>
        <path d="M183.5 960.5H141.5V1027.5H183.5V960.5Z"/>
        <path d="M294.5 923.5H195.5V952.5H294.5V923.5Z"/>
        <path d="M294.5 960.5H195.5V989.5H294.5V960.5Z"/>
        <path d="M294.5 998.5H195.5V1027.5H294.5V998.5Z"/>
        <path d="M516.5 923.5H417.5V952.5H516.5V923.5Z"/>
        <path d="M627.5 923.5H528.5V952.5H627.5V923.5Z"/>
    </g>

<text fill="#EEEEEE" font-family="KH Interference" font-size="20" letter-spacing="0em"><tspan x="115.027" y="1067">${SVGElementHelper.convertDateMonth(player.getJoinDate())}</tspan></text>
<text fill="#EEEEEE" font-family="KH Interference" font-size="20" letter-spacing="0em"><tspan x="249.027" y="1067">#${player.getGlobalRank()!=null?FormatHelper.transformNumber(player.getGlobalRank()):"-"}</tspan></text>
<text fill="#EEEEEE" font-family="KH Interference" font-size="20" letter-spacing="0em"><tspan x="387.027" y="1067">LVL.${player.getLevel()}</tspan></text>
<text fill="#EEEEEE" font-family="KH Interference" font-size="20" letter-spacing="0em"><tspan x="532.023" y="1067">${FormatHelper.formatDouble(player.getAccuracy(),2)}%</tspan></text>
</svg>
