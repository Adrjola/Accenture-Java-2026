PRAGMA foreign_keys = ON;

-- Delete students first because students depends on courses.
DELETE FROM students;
DELETE FROM courses;

-- Insert 3 courses.
-- Remember:
-- course_id is a number.
-- course_name must be present.
-- credits must be greater than 0.
INSERT INTO courses (course_id, course_name, credits) VALUES
    (1, 'Java Basics', 5),
    (2, 'Databases', 4),
    (3, 'Web Development', 6);

-- Insert 5 students.
-- Remember:
-- id is a number.
-- name must be present.
-- email must be unique.
-- age must be 18 or older.
-- course_id must exist in the courses table.
INSERT INTO students (id, name, email, age, course_id) VALUES
    (1, 'Anna Smith', 'anna.smith@example.com', 19, 1),
    (2, 'John Brown', 'john.brown@example.com', 22, 2),
    (3, 'Marta Green', 'marta.green@example.com', 21, 1),
    (4, 'Peter White', 'peter.white@example.com', 24, 3),
    (5, 'Laura Black', 'laura.black@example.com', 18, 2);
