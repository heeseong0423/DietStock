package com.fournineseven.dietstock

object DataUtil {
    fun getCSStockData(): List<CSStock> {
        return listOf(
                CSStock(
                        createdAt = 0,
                        open = 0f,
                        close = 1778.4f,
                        shadowHigh = 2004.8f,
                        shadowLow = -305.0F
                ),
                CSStock(
                        createdAt = 1,
                        open = 1778.4f,
                        close = 1435.8f,
                        shadowHigh = 1574.9f,
                        shadowLow = 350.4f
                ),
                CSStock(
                        createdAt = 2,
                        open = 1435.8f,
                        close = 221.9F,
                        shadowHigh = 868.4f,
                        shadowLow = -648f
                ),
                CSStock(
                        createdAt = 3,
                        open = 211.9f,
                        close = 864.2F,
                        shadowHigh = 1500.7F,
                        shadowLow = 222.1F
                ),
                CSStock(
                        createdAt = 4,
                        open = 864.2F,
                        close = 1876.4F,
                        shadowHigh = 2452.7F,
                        shadowLow = 0F
                ),
                CSStock(
                        createdAt = 5,
                        open = 1876.4F,
                        close = 137.2F,
                        shadowHigh = 2225.0F,
                        shadowLow = 0F
                ),
                CSStock(
                        createdAt = 6,
                        open = 137.2F,
                        close = -332.7F,
                        shadowHigh = 831.5F,
                        shadowLow = -435.7F
                ),
                CSStock(
                        createdAt = 7,
                        open = -332.7F,
                        close = 1522.2F,
                        shadowHigh = 1822.5F,
                        shadowLow = 357.8F
                ),
                CSStock(
                        createdAt = 8,
                        open = 1522.2F,
                        close = 2830.7F,
                        shadowHigh = 2985.4F,
                        shadowLow = 0F
                ),
                CSStock(
                        createdAt = 9,
                        open = 2830.7F,
                        close = -851.5F,
                        shadowHigh = 2830.7F,
                        shadowLow = -907.4F
                ),
                CSStock(
                        createdAt = 10,
                        open = -851.5F,
                        close = -448.4F,
                        shadowHigh = -157.1F,
                        shadowLow = -851.5F
                ),
                CSStock(
                        createdAt = 11,
                        open = -448.4F,
                        close = 101.7F,
                        shadowHigh = 120.7F,
                        shadowLow = -448.4F
                ),
                CSStock(
                        createdAt = 12,
                        open = 101.7f,
                        close = 12.5F,
                        shadowHigh = 120.7F,
                        shadowLow = -5.1F
                ),
                CSStock(
                        createdAt = 13,
                        open = 12.5F,
                        close = 528.4F,
                        shadowHigh = 605.5F,
                        shadowLow = 12.5F
                )
        )
    }
}