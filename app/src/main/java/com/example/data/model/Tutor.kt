package com.example.data.model

data class Tutor(
    val id: String,
    val name: String,
    val title: String,
    val subject: String,
    val category: TutorCategory,
    val classLevel: String, // e.g., "Class 9-12 (SSC/HSC)", "Class 1-8", "University", "All Levels"
    val language: String, // "Bangla & English", "Bangla", "English"
    val mode: TutorMode, // ONLINE, OFFLINE, HYBRID
    val location: String, // "Dhaka", "Chittagong", "Sylhet", "Remote / Worldwide"
    val experience: String, // "6+ Years Experience"
    val teachingMethod: String, // "Interactive problem solving, conceptual notes & mock tests"
    val hourlyRate: String, // "৳ 500 / hr" or "Free / Open Access"
    val rating: Double,
    val reviewCount: Int,
    val verified: Boolean = true,
    val bio: String,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val bookingLink: String? = null,
    val directPortalUrl: String? = null,
    val isEducationalPortal: Boolean = false
)

enum class TutorCategory(val label: String) {
    ALL("All Categories"),
    SCHOOL("School (Class 1-10)"),
    COLLEGE("College & HSC (Class 11-12)"),
    ONLINE("Online Tutors"),
    MATHEMATICS("Mathematics"),
    SCIENCE("Science & Physics"),
    ENGLISH("English Language"),
    PROGRAMMING("Programming & CS"),
    UNIVERSITY("University Subjects"),
    FREE_RESOURCES("Free Resources")
}

enum class TutorMode(val label: String) {
    ALL("All Modes"),
    ONLINE("Online"),
    OFFLINE("In-Person"),
    HYBRID("Hybrid")
}
