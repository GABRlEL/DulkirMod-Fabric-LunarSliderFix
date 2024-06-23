package com.dulkirfabric.features

import com.dulkirfabric.config.DulkirConfig
import com.dulkirfabric.events.LongUpdateEvent
import com.dulkirfabric.events.WorldRenderLastEvent
import com.dulkirfabric.util.ScoreBoardUtils
import com.dulkirfabric.util.TablistUtils
import com.dulkirfabric.util.Utils
import com.dulkirfabric.util.render.WorldRenderUtils
import meteordevelopment.orbit.EventHandler
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.Vec3d

object EffigyDisplay {
    private var effigyWaypoints = arrayOf(
        Effigy(Vec3d(150.5, 76.0, 95.5)),
        Effigy(Vec3d(193.5, 90.0, 119.5)),
        Effigy(Vec3d(235.5, 107.0, 147.5)),
        Effigy(Vec3d(293.5, 93.0, 134.5)),
        Effigy(Vec3d(262.5, 96.0, 94.5)),
        Effigy(Vec3d(240.5, 126.0, 118.5))
    )

    private val c7OnlyRegex = Regex("[^c7]")

    @EventHandler
    fun onRender(event: WorldRenderLastEvent) {
        // if we have any waypoints that need rendering, Do so.
        for (effigy in effigyWaypoints) {
            if (effigy.render) {
                WorldRenderUtils.renderWaypoint(
                    Text.literal("Inactive").setStyle(Style.EMPTY.withColor(Formatting.GOLD)), event.context, effigy.coords
                )
            }
        }
    }

    @EventHandler
    fun checkEffigies(event: LongUpdateEvent) {
        if (!DulkirConfig.configOptions.inactiveEffigyDisplay) return
        if (!Utils.isInSkyblock()) return
        if (TablistUtils.persistentInfo.area != "The Rift") {
            effigyWaypoints.forEach { it.render = false }
            return
        }
        val lines = ScoreBoardUtils.getLinesWithColor() ?: return
        if (lines.size <= 7) return
        if (!lines[3].contains("Stillgore")) return
        val effigyStatusLine = lines[6].replace(c7OnlyRegex, "").substring(1)
        if (effigyStatusLine.length != 6) return
        for (i in 0..5) {
            effigyWaypoints[i].render = (effigyStatusLine[i] == '7')
        }
    }

    /**
     * data class for storing the effigy coordinates and whether they need to be rendered
     */
    data class Effigy(val coords: Vec3d, var render: Boolean = false)
}