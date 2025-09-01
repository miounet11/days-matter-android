package com.coquankedian.bigtime.utils

import java.util.*

/**
 * Simple Lunar Calendar utility class
 * This is a simplified implementation for basic lunar date conversion
 * For production use, consider using a proper lunar calendar library
 */
class LunarCalendar {

    companion object {
        // Lunar calendar data (simplified for demo purposes)
        // In a real implementation, you would use comprehensive lunar calendar data
        private val lunarMonths = arrayOf(
            "正月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "腊月"
        )

        private val lunarDays = arrayOf(
            "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
        )

        // Simplified lunar calendar calculations
        // This is a basic approximation and not astronomically accurate
        private const val LUNAR_MONTH_DAYS = 29.53059 // Average lunar month length
        private const val LUNAR_YEAR_DAYS = 354.36708 // Average lunar year length
        
        // Base lunar date (农历2024年正月初一对应的公历日期)
        // This would need to be updated with accurate lunar calendar data
        private val baseLunarDate = Calendar.getInstance().apply {
            set(2024, Calendar.FEBRUARY, 10) // 2024年2月10日对应农历2024年正月初一
        }

        fun solarToLunar(solarDate: Date): LunarDate {
            return try {
                val solarCalendar = Calendar.getInstance()
                solarCalendar.time = solarDate
                
                // Calculate days difference from base date
                val diffInMillis = solarDate.time - baseLunarDate.timeInMillis
                val diffInDays = (diffInMillis / (24 * 60 * 60 * 1000)).toInt()
                
                // Simple approximation
                var lunarYear = 2024
                var lunarMonth = 1
                var lunarDay = 1
                
                var remainingDays = diffInDays
                
                // Rough calculation (not astronomically accurate)
                if (remainingDays > 0) {
                    val years = (remainingDays / LUNAR_YEAR_DAYS).toInt()
                    lunarYear += years
                    remainingDays -= (years * LUNAR_YEAR_DAYS).toInt()
                    
                    val months = (remainingDays / LUNAR_MONTH_DAYS).toInt()
                    lunarMonth = ((lunarMonth + months - 1) % 12) + 1
                    remainingDays -= (months * LUNAR_MONTH_DAYS).toInt()
                    
                    lunarDay = maxOf(1, remainingDays + 1)
                } else if (remainingDays < 0) {
                    remainingDays = -remainingDays
                    val years = (remainingDays / LUNAR_YEAR_DAYS).toInt()
                    lunarYear -= years
                    remainingDays -= (years * LUNAR_YEAR_DAYS).toInt()
                    
                    val months = (remainingDays / LUNAR_MONTH_DAYS).toInt()
                    lunarMonth = ((12 - months) % 12) + 1
                    remainingDays -= (months * LUNAR_MONTH_DAYS).toInt()
                    
                    lunarDay = maxOf(1, (LUNAR_MONTH_DAYS - remainingDays).toInt())
                }
                
                // Ensure valid ranges
                lunarMonth = maxOf(1, minOf(12, lunarMonth))
                lunarDay = maxOf(1, minOf(30, lunarDay))
                
                LunarDate(lunarYear, lunarMonth, lunarDay)
            } catch (e: Exception) {
                // Return a default lunar date if calculation fails
                LunarDate(2024, 1, 1)
            }
        }

        fun lunarToSolar(lunarYear: Int, lunarMonth: Int, lunarDay: Int): Date {
            return try {
                // Simple approximation to convert lunar date back to solar
                val yearDiff = lunarYear - 2024
                val monthDiff = lunarMonth - 1
                val dayDiff = lunarDay - 1
                
                val totalDays = (yearDiff * LUNAR_YEAR_DAYS + 
                               monthDiff * LUNAR_MONTH_DAYS + 
                               dayDiff).toLong()
                
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = baseLunarDate.timeInMillis + (totalDays * 24 * 60 * 60 * 1000)
                calendar.time
            } catch (e: Exception) {
                Date()
            }
        }

        fun formatLunarDate(lunarDate: LunarDate): String {
            val monthName = if (lunarDate.month in 1..12) {
                lunarMonths[lunarDate.month - 1]
            } else "正月"
            
            val dayName = if (lunarDate.day in 1..30) {
                lunarDays[lunarDate.day - 1]
            } else "初一"
            
            return "${lunarDate.year}年$monthName$dayName"
        }

        fun getLunarMonthName(month: Int): String {
            return if (month in 1..12) lunarMonths[month - 1] else "正月"
        }

        fun getLunarDayName(day: Int): String {
            return if (day in 1..30) lunarDays[day - 1] else "初一"
        }
    }

    data class LunarDate(
        val year: Int,
        val month: Int, // 1-12
        val day: Int    // 1-30
    ) {
        override fun toString(): String {
            return formatLunarDate(this)
        }
    }
}