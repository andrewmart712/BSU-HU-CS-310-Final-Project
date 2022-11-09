/* Put your final project reporting queries here */
USE cs_hu_310_final_project;

-- 1. Calculate the GPA for student given a student_id (use student_id = 1)
SELECT 
	first_name, last_name, COUNT(class_registrations.student_id) AS number_of_classes, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points_earned, AVG(convert_to_grade_point(letter_grade)) AS GPA
FROM students
JOIN class_registrations ON class_registrations.student_id = students.student_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
WHERE class_registrations.student_id = 1
GROUP BY class_registrations.student_id;

-- 2. Calculate the GPA for each student (across all classes and all terms)
SELECT 
	first_name, last_name, COUNT(class_registrations.student_id) AS number_of_classes, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points_earned, AVG(convert_to_grade_point(letter_grade)) AS GPA
FROM students
JOIN class_registrations ON class_registrations.student_id = students.student_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
GROUP BY class_registrations.student_id;

-- 3. Calculate the avg GPA for each class
SELECT
	code, name, COUNT(class_registrations.grade_id) AS number_of_grades, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points, AVG(convert_to_grade_point(letter_grade)) AS 'AVG GPA'
FROM classes
JOIN class_sections ON class_sections.class_id = classes.class_id
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
GROUP BY class_sections.class_id;

-- 4. Calculate the avg GPA for each class and term
SELECT
	code, classes.name, terms.name AS term, COUNT(class_registrations.grade_id) AS number_of_grades, SUM(convert_to_grade_point(letter_grade)) AS total_grade_points, AVG(convert_to_grade_point(letter_grade)) AS 'AVG GPA'
FROM classes
JOIN class_sections ON class_sections.class_id = classes.class_id
JOIN terms ON terms.term_id = class_sections.term_id
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN grades ON grades.grade_id = class_registrations.grade_id
GROUP BY class_sections.class_section_id;

-- 5. List all the classes being taught by an instructor (use instructor_id=1)
SELECT
	first_name, last_name, title, code, classes.name AS class_name, terms.name AS term
FROM instructors
JOIN academic_titles ON academic_titles.academic_title_id = instructors.academic_title_id
JOIN class_sections ON class_sections.instructor_id = instructors.instructor_id
JOIN classes ON classes.class_id = class_sections.class_id
JOIN terms ON terms.term_id = class_sections.term_id
WHERE instructors.instructor_id = 1;

-- 6. List all classes with terms & instructor
SELECT
	code, classes.name, terms.name AS term, first_name, last_name
FROM classes
JOIN class_sections ON class_sections.class_id = classes.class_id
JOIN terms ON terms.term_id = class_sections.term_id
JOIN instructors ON instructors.instructor_id = class_sections.instructor_id;

-- 7. Calulate the remaining space left in a class
SELECT
	code, classes.name, terms.name AS term, COUNT(class_registrations.student_id) AS enrolled_students, (maximum_students - COUNT(class_registrations.student_id)) AS space_remaining
FROM classes
JOIN class_sections ON class_sections.class_id = classes.class_id
JOIN terms ON terms.term_id = class_sections.term_id
JOIN class_registrations ON class_registrations.class_section_id = class_sections.class_section_id
JOIN students ON students.student_id = class_registrations.student_id
GROUP BY class_sections.class_section_id;
