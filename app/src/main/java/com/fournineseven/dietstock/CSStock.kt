package com.fournineseven.dietstock

data class CSStock(
        var createdAt: Long = 0,
        var open: Float,
        var close:Float,
        var shadowHigh:Float,
        var shadowLow:Float
)
