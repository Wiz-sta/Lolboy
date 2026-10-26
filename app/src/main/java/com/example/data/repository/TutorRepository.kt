package com.example.data.repository

import com.example.data.model.Tutor
import com.example.data.model.TutorCategory
import com.example.data.model.TutorMode

class TutorRepository {

    private val allTutorsAndResources: List<Tutor> = listOf(
        // Educational Portals (Direct Links & Free Resources)
        Tutor(
            id = "portal_10ms",
            name = "10 Minute School",
            title = "Bangladesh's Premier Online Learning Platform",
            subject = "Class 1-12, HSC, Admission, English, Coding",
            category = TutorCategory.SCHOOL,
            classLevel = "Class 1–12 & Admission",
            language = "Bangla",
            mode = TutorMode.ONLINE,
            location = "Dhaka / Online Worldwide",
            experience = "10+ Years Educational Leader",
            teachingMethod = "Animated video lessons, interactive quizzes, live masterclasses and revision notes",
            hourlyRate = "Free & Subscription Courses",
            rating = 4.9,
            reviewCount = 142000,
            verified = true,
            bio = "Comprehensive national curriculum coverage for SSC, HSC, Varsity admission, BCS, and skill development courses.",
            directPortalUrl = "https://10minuteschool.com",
            isEducationalPortal = true
        ),
        Tutor(
            id = "portal_khan",
            name = "Khan Academy",
            title = "Free World-Class Education for Anyone, Anywhere",
            subject = "Mathematics, Physics, Chemistry, Biology, Computing",
            category = TutorCategory.FREE_RESOURCES,
            classLevel = "Class 1–12 & University",
            language = "Bangla & English",
            mode = TutorMode.ONLINE,
            location = "Remote / Worldwide",
            experience = "15+ Years Global Education",
            teachingMethod = "Mastery-based personalized learning dashboard, step-by-step video tutorials and practice drills",
            hourlyRate = "100% Free Forever",
            rating = 4.95,
            reviewCount = 520000,
            verified = true,
            bio = "Non-profit educational platform providing personalized learning from elementary arithmetic to advanced calculus.",
            directPortalUrl = "https://www.khanacademy.org",
            isEducationalPortal = true
        ),
        Tutor(
            id = "portal_shikho",
            name = "Shikho Learning",
            title = "Visual & Live Interactive Learning for Bangladeshi Curriculum",
            subject = "Math, Higher Math, Physics, Chemistry, Biology",
            category = TutorCategory.SCIENCE,
            classLevel = "Class 6–12 (SSC & HSC)",
            language = "Bangla",
            mode = TutorMode.ONLINE,
            location = "Dhaka / Online",
            experience = "5+ Years Modern EdTech",
            teachingMethod = "3D visual animation explanations, daily live doubt-solving sessions with top university mentors",
            hourlyRate = "Course Packages Available",
            rating = 4.8,
            reviewCount = 48000,
            verified = true,
            bio = "Revolutionizing education in Bangladesh through high quality visual pedagogy and personalized exam preparation.",
            directPortalUrl = "https://shikho.com",
            isEducationalPortal = true
        ),
        Tutor(
            id = "portal_mit_ocw",
            name = "MIT OpenCourseWare",
            title = "Massachusetts Institute of Technology Open Course Materials",
            subject = "Calculus, Linear Algebra, Algorithms, Quantum Physics",
            category = TutorCategory.UNIVERSITY,
            classLevel = "University (Undergrad & Postgrad)",
            language = "English",
            mode = TutorMode.ONLINE,
            location = "Cambridge, MA / Worldwide",
            experience = "20+ Years Open University Materials",
            teachingMethod = "Actual MIT classroom lectures, problem sets with solutions, exams and syllabus",
            hourlyRate = "100% Free Open Access",
            rating = 5.0,
            reviewCount = 890000,
            verified = true,
            bio = "Official web publication of virtually all MIT course content, open and available to the entire world.",
            directPortalUrl = "https://ocw.mit.edu",
            isEducationalPortal = true
        ),
        Tutor(
            id = "portal_bbc_english",
            name = "BBC Learning English",
            title = "Global Standard English Tutoring & Resources",
            subject = "Spoken English, Grammar, Vocabulary, Pronunciation",
            category = TutorCategory.ENGLISH,
            classLevel = "All Levels (Beginner to Advanced)",
            language = "English & Bangla Support",
            mode = TutorMode.ONLINE,
            location = "London / Worldwide",
            experience = "75+ Years BBC World Service",
            teachingMethod = "6 Minute English audio stories, grammar games, quizzes, news-based vocabulary drills",
            hourlyRate = "100% Free",
            rating = 4.9,
            reviewCount = 310000,
            verified = true,
            bio = "Master everyday conversational English, IELTS preparation, and professional business communication.",
            directPortalUrl = "https://www.bbc.co.uk/learningenglish",
            isEducationalPortal = true
        ),
        Tutor(
            id = "portal_codecademy",
            name = "Codecademy & FreeCodeCamp",
            title = "Interactive Coding & Software Engineering",
            subject = "Python, JavaScript, Web Development, Java, SQL",
            category = TutorCategory.PROGRAMMING,
            classLevel = "Class 8-12 & University",
            language = "English",
            mode = TutorMode.ONLINE,
            location = "Online / Global",
            experience = "12+ Years Coding Academy",
            teachingMethod = "In-browser hands-on coding exercises, project-based portfolio builds, instant feedback",
            hourlyRate = "Free Core Curriculum",
            rating = 4.85,
            reviewCount = 275000,
            verified = true,
            bio = "Learn to code interactively from scratch. Build real apps, algorithms, and web applications.",
            directPortalUrl = "https://www.codecademy.com",
            isEducationalPortal = true
        ),

        // Verified Specialist Tutors
        Tutor(
            id = "tutor_rafiq",
            name = "Engr. Rafiqul Islam, M.Sc.",
            title = "Senior Mathematics & Higher Math Specialist",
            subject = "Higher Mathematics, Calculus, Geometry & Algebra",
            category = TutorCategory.MATHEMATICS,
            classLevel = "Class 9–12 (SSC & HSC) + Varsity Admission",
            language = "Bangla & English",
            mode = TutorMode.HYBRID,
            location = "Dhanmondi, Dhaka & Online Zoom",
            experience = "8 Years (Ex-BUET Mentor)",
            teachingMethod = "Deep conceptual derivation, shortcut techniques for MCQs, weekly chapter tests and notes",
            hourlyRate = "৳ 600 / hr",
            rating = 4.95,
            reviewCount = 84,
            verified = true,
            bio = "Helped 150+ students achieve GPA 5.0 and crack BUET/DU engineering admission tests with clarity.",
            contactEmail = "rafiq.math.tutor@example.com",
            contactPhone = "+880 1711-234567"
        ),
        Tutor(
            id = "tutor_tahmina",
            name = "Dr. Tahmina Akter",
            title = "Physics & Physical Science Educator",
            subject = "Physics (Mechanics, Optics, Thermodynamics, Modern Physics)",
            category = TutorCategory.SCIENCE,
            classLevel = "Class 9–12 & Medical/Engineering Prep",
            language = "Bangla & English",
            mode = TutorMode.ONLINE,
            location = "Chittagong & Online Worldwide",
            experience = "7 Years Teaching Experience",
            teachingMethod = "Real-life physical experiment demos, formula derivation sheets, and past 10 years board question analysis",
            hourlyRate = "৳ 550 / hr",
            rating = 4.9,
            reviewCount = 62,
            verified = true,
            bio = "Ph.D. in Applied Physics with a passion for turning complex physics equations into simple intuitive ideas.",
            contactEmail = "tahmina.physics@example.com"
        ),
        Tutor(
            id = "tutor_shakil",
            name = "Shakil Ahmed, B.Sc. in CSE",
            title = "Full-Stack & Python Programming Mentor",
            subject = "Python, Kotlin/Android, Data Structures, Web Development",
            category = TutorCategory.PROGRAMMING,
            classLevel = "Class 8-12, College & University Students",
            language = "Bangla & English",
            mode = TutorMode.ONLINE,
            location = "Uttara, Dhaka & Online Google Meet",
            experience = "5 Years Professional Developer & Instructor",
            teachingMethod = "Hands-on project building, live pair programming, GitHub code reviews and homework assistance",
            hourlyRate = "৳ 700 / hr",
            rating = 4.92,
            reviewCount = 47,
            verified = true,
            bio = "Passionate about empowering young coders to build real Android applications and web software.",
            contactEmail = "shakil.codes@example.com",
            contactPhone = "+880 1819-987654"
        ),
        Tutor(
            id = "tutor_ananya",
            name = "Ananya Roy, MA in English",
            title = "IELTS 8.5 & Academic English Tutor",
            subject = "English 1st & 2nd Paper, Grammar, Creative Writing, IELTS",
            category = TutorCategory.ENGLISH,
            classLevel = "Class 6–12 & University Candidates",
            language = "English & Bangla",
            mode = TutorMode.ONLINE,
            location = "Sylhet & Remote",
            experience = "6 Years Language Coaching",
            teachingMethod = "Accent neutralisation, daily vocabulary building, essay structure breakdown and mock interviews",
            hourlyRate = "৳ 500 / hr",
            rating = 4.88,
            reviewCount = 73,
            verified = true,
            bio = "Dedicated English instructor helping students write persuasive essays and speak with natural fluency.",
            contactEmail = "ananya.english@example.com"
        ),
        Tutor(
            id = "tutor_mahbub",
            name = "Mahbubur Rahman, M.Sc.",
            title = "Junior School General Science & Math Coach",
            subject = "General Science, Basic Mathematics, ICT",
            category = TutorCategory.SCHOOL,
            classLevel = "Class 1–8 (Primary & Junior High)",
            language = "Bangla",
            mode = TutorMode.OFFLINE,
            location = "Mirpur, Dhaka",
            experience = "9 Years School Tutoring",
            teachingMethod = "Patient, engaging storytelling methods, visual flashcards, and homework tracking",
            hourlyRate = "৳ 450 / hr",
            rating = 4.86,
            reviewCount = 59,
            verified = true,
            bio = "Specialized in building strong foundations for young students without rote memorization stress.",
            contactEmail = "mahbub.tutor@example.com",
            contactPhone = "+880 1912-345678"
        ),
        Tutor(
            id = "tutor_university_cs",
            name = "Prof. Dr. K. M. Hossain",
            title = "University Computer Science & Algorithms Professor",
            subject = "Algorithms, Discrete Mathematics, Operating Systems, Database Systems",
            category = TutorCategory.UNIVERSITY,
            classLevel = "University Undergrad & Thesis Research",
            language = "English & Bangla",
            mode = TutorMode.ONLINE,
            location = "Dhaka & Worldwide Online",
            experience = "14 Years University Faculty",
            teachingMethod = "Theoretical rigor, research paper reading, complex problem proofs, and competitive programming guidance",
            hourlyRate = "৳ 1,000 / hr",
            rating = 4.97,
            reviewCount = 112,
            verified = true,
            bio = "Former ACM ICPC coach guiding undergrads through algorithms and engineering research.",
            contactEmail = "prof.hossain.cs@example.com"
        )
    )

    fun getTutors(
        category: TutorCategory = TutorCategory.ALL,
        mode: TutorMode = TutorMode.ALL,
        searchQuery: String = "",
        languageFilter: String = "All"
    ): List<Tutor> {
        return allTutorsAndResources.filter { tutor ->
            val matchesCategory = when (category) {
                TutorCategory.ALL -> true
                TutorCategory.FREE_RESOURCES -> tutor.isEducationalPortal || tutor.hourlyRate.contains("Free", ignoreCase = true)
                TutorCategory.ONLINE -> tutor.mode == TutorMode.ONLINE || tutor.mode == TutorMode.HYBRID
                else -> tutor.category == category || tutor.subject.contains(category.label, ignoreCase = true)
            }

            val matchesMode = when (mode) {
                TutorMode.ALL -> true
                else -> tutor.mode == mode || tutor.mode == TutorMode.HYBRID
            }

            val matchesLanguage = when (languageFilter) {
                "All" -> true
                "Bangla" -> tutor.language.contains("Bangla", ignoreCase = true)
                "English" -> tutor.language.contains("English", ignoreCase = true)
                else -> true
            }

            val matchesQuery = if (searchQuery.isBlank()) true else {
                tutor.name.contains(searchQuery, ignoreCase = true) ||
                tutor.subject.contains(searchQuery, ignoreCase = true) ||
                tutor.title.contains(searchQuery, ignoreCase = true) ||
                tutor.classLevel.contains(searchQuery, ignoreCase = true) ||
                tutor.location.contains(searchQuery, ignoreCase = true)
            }

            matchesCategory && matchesMode && matchesLanguage && matchesQuery
        }
    }
}
