@import me.aloic.lazybot.osu.dao.entity.vo.MapPpAnalysisView
@param MapPpAnalysisView model
<?xml version="1.0" encoding="UTF-8"?>
<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
     width="${model.width()}" height="${model.height()}"
     viewBox="0 0 ${model.width()} ${model.height()}" fill="none">
  <defs>
    <linearGradient id="coverShade" x1="700" y1="570" x2="700" y2="60" gradientUnits="userSpaceOnUse">
      <stop stop-opacity=".72"/>
      <stop offset=".42" stop-opacity=".16"/>
      <stop offset="1" stop-opacity="0"/>
    </linearGradient>
    <style>
      .heading { font-family: Outfit, sans-serif; font-weight: 500; fill: #0D0D0D; }
      .body { font-family: Outfit-Regular, Outfit, sans-serif; fill: #0D0D0D; }
      .muted { font-family: Outfit-Regular, Outfit, sans-serif; fill: #777; }
      .micro { font-family: Outfit-Regular, Outfit, sans-serif; fill: #777; font-size: 9px; }
      .grid { stroke: #D1D1D1; stroke-width: 1; }
      .rule { stroke: #0D0D0D; stroke-width: 1; }
      .curve { stroke: #0D0D0D; stroke-width: 2; stroke-linecap: round; }
    </style>
  </defs>

  <rect width="1400" height="1520" fill="#F2F2F2"/>

  <!-- Mapper / request context -->
  <image xlink:href="${model.analysis().context().getMapperAvatarUrl()}"
         x="10" y="10" width="40" height="40" preserveAspectRatio="xMidYMid slice"/>
  <text x="68" y="29" class="body" font-size="13">${model.analysis().context().getBeatmap().getCreator()}</text>
  <text x="68" y="44" class="muted" font-size="9">MAPPER</text>
  <line x1="227" y1="10" x2="227" y2="50" class="rule"/>
  <text x="250" y="29" class="body" font-size="13">${model.generatedAt()}</text>
  <text x="250" y="44" class="muted" font-size="9">GENERATED AT</text>
  <line x1="503" y1="10" x2="503" y2="50" class="rule"/>
  <text x="527" y="29" class="body" font-size="13">${model.scenario()}</text>
  <text x="527" y="44" class="muted" font-size="9">MOD</text>
  <line x1="718" y1="10" x2="718" y2="50" class="rule"/>
  <text x="742" y="29" class="body" font-size="13">${String.format(java.util.Locale.ROOT, "%.2f%%", model.analysis().targetAccuracy())}</text>
  <text x="742" y="44" class="muted" font-size="9">TARGET ACCURACY</text>
  <line x1="1310" y1="10" x2="1310" y2="50" class="rule"/>
  <text x="1336" y="35" class="body" font-size="14">osu!</text>
  <line x1="0" y1="59.5" x2="1400" y2="59.5" class="rule"/>

  <!-- Beatmap cover -->
  <image xlink:href="${model.analysis().context().getMapBackgroundUrl()}"
         x="0" y="60" width="1400" height="510" preserveAspectRatio="xMidYMid slice"/>
  <rect x="0" y="60" width="1400" height="510" fill="url(#coverShade)"/>
  <text x="26" y="493" fill="white" font-family="Outfit" font-size="52" font-weight="500">${model.analysis().context().getBeatmap().getArtist()} - ${model.analysis().context().getBeatmap().getTitle()}</text>
  <text x="26" y="535" fill="white" font-family="Outfit" font-size="28">[${model.analysis().context().getBeatmap().getVersion()}]</text>
  <text x="1372" y="535" text-anchor="end" fill="white" font-family="Outfit" font-size="18">BID ${model.analysis().context().getBeatmap().getBid()}</text>

  <!-- Historical PP -->
  <g id="history">
    <text x="23" y="626" class="heading" font-size="28">Historical PP</text>
    <text x="23" y="643" class="micro">Absolute PP and change from the preceding algorithm</text>
    <text x="576" y="611" text-anchor="end" class="micro">PreCSR → Now</text>
    <text x="576" y="626" text-anchor="end" class="heading" font-size="12">${model.cumulativeChange()}</text>
    <line x1="587.5" y1="603" x2="587.5" y2="633" class="rule"/>
    <text x="599" y="611" class="micro">Largest Change</text>
    <text x="599" y="626" class="heading" font-size="12">${model.largestChange()}</text>

    @for (int i = 0; i <= 4; i++)
      <line x1="23" y1="${943 - i * 61}" x2="668" y2="${943 - i * 61}" class="grid"/>
    @endfor
    @for (var bar : model.historyBars())
      <rect x="${bar.x()}" y="${bar.y()}" width="${bar.width()}" height="${bar.height()}"
            fill="${bar.latest() ? "#B8E9E5" : "#D6D6D6"}"/>
      <line x1="${bar.x()}" y1="${bar.y()}" x2="${bar.x() + bar.width()}" y2="${bar.y()}" class="rule"/>
      <text x="${bar.x() + bar.width() / 2}" y="${bar.y() - 8}" text-anchor="middle" class="heading" font-size="10">${bar.pp()}</text>
      <text x="${bar.x() + bar.width() / 2}" y="962" text-anchor="middle" class="body" font-size="10">${bar.algorithm()}</text>
      <text x="${bar.x() + bar.width() / 2}" y="979" text-anchor="middle" class="muted" font-size="8">${bar.change()}</text>
    @endfor
  </g>

  <line x1="699.5" y1="598" x2="699.5" y2="993" class="rule"/>

  <!-- PP composition -->
  <g id="composition">
    <text x="732" y="626" class="heading" font-size="28">PP Composition</text>
    <text x="732" y="643" class="micro">Normalized component weight (component PP¹·¹), not an additive total</text>
    <rect x="1043" y="611" width="9" height="9" fill="#B8E9E5"/><text x="1057" y="619" class="body" font-size="9">Aim</text>
    <rect x="1091" y="611" width="9" height="9" fill="#E9B8BF"/><text x="1105" y="619" class="body" font-size="9">Speed</text>
    <rect x="1149" y="611" width="9" height="9" fill="#B8BFE9"/><text x="1163" y="619" class="body" font-size="9">Reading</text>
    <rect x="1219" y="611" width="9" height="9" fill="#E9DDB8"/><text x="1233" y="619" class="body" font-size="9">Accuracy</text>
    <rect x="1293" y="611" width="9" height="9" fill="#CBE9B8"/><text x="1307" y="619" class="body" font-size="9">Flashlight</text>

    @for (var row : model.breakdownRows())
      <text x="734" y="${row.segments().getFirst().y() + 12}" class="heading" font-size="11">${row.algorithm()}</text>
      <text x="826" y="${row.segments().getFirst().y() + 12}" text-anchor="end" class="muted" font-size="9">${row.totalPp()}</text>
      @for (var segment : row.segments())
        <rect x="${segment.x()}" y="${segment.y()}" width="${segment.width()}" height="${segment.height()}" fill="${segment.color()}"/>
        @if (segment.width() >= 42)
          <text x="${segment.x() + segment.width() / 2}" y="${segment.y() + 17}" text-anchor="middle" class="body" font-size="8">${String.format(java.util.Locale.ROOT, "%.1f%%", segment.ratio())}</text>
        @endif
      @endfor
      <text x="837" y="${row.segments().getFirst().y() + 40}" class="muted" font-size="8">
        @for (int i = 0; i < row.segments().size(); i++)
          ${i == 0 ? "" : "  /  "}${row.segments().get(i).name()} ${String.format(java.util.Locale.ROOT, "%.2fpp", row.segments().get(i).pp())}
        @endfor
      </text>
    @endfor
  </g>

  <line x1="0" y1="1013.5" x2="1400" y2="1013.5" class="rule"/>

  <!-- Miss sensitivity -->
  <g id="miss-curve">
    <text x="23" y="1062" class="heading" font-size="28">Miss Sensitivity</text>
    <text x="23" y="1079" class="micro">Latest algorithm with accuracy being fixed at ${String.format(java.util.Locale.ROOT, "%.2f%%", model.analysis().targetAccuracy())}</text>
    @for (int index : new int[] {1, 5, 10, 20})
      !{int offset = index == 1 ? 0 : index == 5 ? 189 : index == 10 ? 378 : 567;}
      <line x1="${26 + offset}" y1="1094" x2="${26 + offset}" y2="1138" class="rule"/>
      <text x="${37 + offset}" y="1106" class="micro">${index} MISS</text>
      <text x="${37 + offset}" y="1123" class="heading" font-size="11">${MapPpAnalysisView.formatPp(model.analysis().missCurve().get(index).pp())}</text>
      <text x="${37 + offset}" y="1135" class="muted" font-size="8">${MapPpAnalysisView.signed(-model.analysis().missCurve().get(index).absoluteLoss())}pp / ${MapPpAnalysisView.signed(-model.analysis().missCurve().get(index).relativeLoss())}%</text>
    @endfor
    @for (var tick : model.missChart().yTicks())
      <line x1="${model.missChart().x()}" y1="${tick.y()}" x2="${model.missChart().x() + model.missChart().width()}" y2="${tick.y()}" class="grid"/>
      <text x="${model.missChart().x() - 9}" y="${tick.y() + 3}" text-anchor="end" class="muted" font-size="8">${tick.label()}</text>
    @endfor
    @for (var line : model.missChart().segments())
      <line x1="${line.x1()}" y1="${line.y1()}" x2="${line.x2()}" y2="${line.y2()}" class="curve"/>
    @endfor
    @for (var point : model.missChart().points())
      <circle cx="${point.x()}" cy="${point.y()}" r="${point.labelled() ? 3.5 : 2}" fill="#0D0D0D" stroke="#0D0D0D" stroke-width="1"/>
      @if (point.labelled())
        <text x="${point.x()}" y="1402" text-anchor="middle" class="body" font-size="8">${String.format(java.util.Locale.ROOT, "%.0f", point.input())}</text>
      @endif
    @endfor
    <text x="373" y="1422" text-anchor="middle" class="muted" font-size="9">MISS COUNT</text>
  </g>

  <line x1="699.5" y1="1034" x2="699.5" y2="1429" class="rule"/>

  <!-- Accuracy sensitivity -->
  <g id="accuracy-curve">
    <text x="736" y="1062" class="heading" font-size="28">Accuracy Sensitivity</text>
    <text x="736" y="1079" class="micro">Latest algorithm with 0 miss · sampled every 0.5%</text>
    @for (int index : new int[] {2, 4, 10, 20})
      !{int offset = index == 2 ? 0 : index == 4 ? 189 : index == 10 ? 378 : 567;}
      <line x1="${738 + offset}" y1="1094" x2="${738 + offset}" y2="1138" class="rule"/>
      <text x="${749 + offset}" y="1106" class="micro">${String.format(java.util.Locale.ROOT, "%.0f%%", model.analysis().accuracyCurve().get(index).input())}</text>
      <text x="${749 + offset}" y="1123" class="heading" font-size="11">${MapPpAnalysisView.formatPp(model.analysis().accuracyCurve().get(index).pp())}</text>
      <text x="${749 + offset}" y="1135" class="muted" font-size="8">${MapPpAnalysisView.signed(-model.analysis().accuracyCurve().get(index).absoluteLoss())}pp / ${MapPpAnalysisView.signed(-model.analysis().accuracyCurve().get(index).relativeLoss())}%</text>
    @endfor
    @for (var tick : model.accuracyChart().yTicks())
      <line x1="${model.accuracyChart().x()}" y1="${tick.y()}" x2="${model.accuracyChart().x() + model.accuracyChart().width()}" y2="${tick.y()}" class="grid"/>
      <text x="${model.accuracyChart().x() - 9}" y="${tick.y() + 3}" text-anchor="end" class="muted" font-size="8">${tick.label()}</text>
    @endfor
    @for (var line : model.accuracyChart().segments())
      <line x1="${line.x1()}" y1="${line.y1()}" x2="${line.x2()}" y2="${line.y2()}" class="curve"/>
    @endfor
    @for (var point : model.accuracyChart().points())
      <circle cx="${point.x()}" cy="${point.y()}" r="${point.labelled() ? 3.5 : 2}" fill="#0D0D0D" stroke="#0D0D0D" stroke-width="1"/>
      @if (point.labelled())
        <text x="${point.x()}" y="1402" text-anchor="middle" class="body" font-size="8">${String.format(java.util.Locale.ROOT, "%.0f%%", point.input())}</text>
      @endif
    @endfor
    <text x="1085" y="1422" text-anchor="middle" class="muted" font-size="9">ACCURACY VALUE</text>
  </g>

  <!-- Footer elements and coordinates mirror Group 5.svg. -->
  <path d="M1400 1460V1459H0V1460V1461H1400V1460Z" fill="#0D0D0D"/>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="15" y="1496.42">BPM</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="67" y="1496.42">${String.format(java.util.Locale.ROOT, "%.2f", model.analysis().context().getBeatmap().getAttributes().getBpm())}</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="200" y="1496.42">Length</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="273" y="1496.42">${MapPpAnalysisView.formatDuration(model.analysis().context().getBeatmap().getAttributes().getLength())}</tspan></text>
  <path d="M380.143 1470V1510" stroke="black"/>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="410" y="1496.42">AR</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="450" y="1496.42">${String.format(java.util.Locale.ROOT, "%.1f", model.analysis().context().getBeatmap().getAttributes().getAr())}</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="550" y="1496.42">OD</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="590" y="1496.42">${String.format(java.util.Locale.ROOT, "%.1f", model.analysis().context().getBeatmap().getAttributes().getOd())}</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="690" y="1496.42">CS</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="730" y="1496.42">${String.format(java.util.Locale.ROOT, "%.1f", model.analysis().context().getBeatmap().getAttributes().getCs())}</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="830" y="1496.42">HP</tspan></text>
  <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="870" y="1496.42">${String.format(java.util.Locale.ROOT, "%.1f", model.analysis().context().getBeatmap().getAttributes().getHp())}</tspan></text>
  <path d="M930 1510H930.5V1470H930H929.5V1510H930Z" fill="#0D0D0D"/>

  <g id="ranked" opacity="${model.status().equals("ranked") ? 1 : 0}">
    <path d="M1297 1474H1261V1510H1297V1474Z" fill="#CEF2C1"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">Ranked</tspan></text>
  </g>
  <g id="approved" opacity="${model.status().equals("approved") ? 1 : 0}">
    <rect x="1281" y="1474" width="36" height="36" fill="#CEF2C1"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">Approved</tspan></text>
  </g>
  <g id="qualified" opacity="${model.status().equals("qualified") ? 1 : 0}">
    <rect x="1274" y="1474" width="36" height="36" fill="#C1E6F2"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">Qualified</tspan></text>
  </g>
  <g id="loved" opacity="${model.status().equals("loved") ? 1 : 0}">
    <rect x="1246" y="1474" width="36" height="36" fill="#FFB2CC"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">Loved</tspan></text>
  </g>
  <g id="pending" opacity="${model.status().equals("pending") ? 1 : 0}">
    <rect x="1266" y="1474" width="36" height="36" fill="#FCDF90"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">Pending</tspan></text>
  </g>
  <g id="wip" opacity="${model.status().equals("wip") ? 1 : 0}">
    <rect x="1236" y="1474" width="36" height="36" fill="#FEA889"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">WIP</tspan></text>
  </g>
  <g id="graveyard" opacity="${model.status().equals("graveyard") ? 1 : 0}">
    <rect x="1286" y="1474" width="36" height="36" fill="#BFBFBF"/>
    <text fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1216" y="1498.42">Graveyard</tspan></text>
  </g>

  <path id="star-rect" d="M1391 1490H1371V1510H1391V1490Z" fill="${model.starBackgroundColor()}"/>
  <text id="star-1" fill="#0D0D0D" font-family="Outfit" font-size="20"><tspan x="1341.49" y="1498.42">${String.format(java.util.Locale.ROOT, "%.2f", model.analysis().starRating())}</tspan></text>
  <mask id="star-clip" maskUnits="userSpaceOnUse" x="1371" y="1490" width="20" height="20">
    <path d="M1391 1490H1371V1510H1391V1490Z" fill="white"/>
  </mask>
  <g mask="url(#star-clip)">
    <text id="star-2" fill="${model.starTextColor()}" font-family="Outfit" font-size="20"><tspan x="1341.49" y="1498.42">${String.format(java.util.Locale.ROOT, "%.2f", model.analysis().starRating())}</tspan></text>
  </g>
  <path d="M1190 1472V1512" stroke="black"/>
</svg>
