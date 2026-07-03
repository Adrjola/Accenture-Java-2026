PRAGMA foreign_keys = ON;

-- Select all courses.
SELECT * FROM courses
ORDER BY course_id;

-- Select all students.
SELECT * FROM students
ORDER BY id;

-- Select students older than 20.
SELECT * FROM students
WHERE age > 20
ORDER BY id;

-- Show student names together with course names.
SELECT students.name, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id
ORDER BY students.id;

-- Count how many students are in each course.
SELECT courses.course_name, COUNT(students.id) AS student_count
FROM courses
LEFT JOIN students ON courses.course_id = students.course_id
GROUP BY courses.course_id, courses.course_name
ORDER BY courses.course_id;

-- Update the first student's age.
UPDATE students
SET age = 20
WHERE id = 1;

-- Check the update.
SELECT * FROM students
WHERE id = 1;

-- Move second student to a different course.
UPDATE students
SET course_id = 3
WHERE id = 2;

-- Check the update using JOIN.
SELECT students.id, students.name, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id
WHERE students.id = 2;

-- Delete one student.
DELETE FROM students
WHERE id = 5;

-- Check the final result.
SELECT students.id, students.name, students.email, students.age, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id
ORDER BY students.id;
