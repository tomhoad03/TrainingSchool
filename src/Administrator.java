import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

// The Administrator class.
public class Administrator {
    private School school;

    // The constructor for Administrator.
    public Administrator(String name) {
        school = new School(name);
    }

    // Runs the simulation for a day.
    public void run() {
        Random rand = new Random();

        // Adds up to two new students to the school.
        for (int i = 0; i < rand.nextInt(3); i++) {
            school.add(createStudent());
        }

        // Adds new instructors to the school.
        if (rand.nextFloat() <= 0.2) {
            school.add(new Teacher("Ross", 'M', 32));
        }
        if (rand.nextFloat() <= 0.1) {
            school.add(new Demonstrator("Rachael", 'F', 30));
        }
        if (rand.nextFloat() <= 0.05) {
            school.add(new OOTrainer("Monica", 'F', 28));
        }
        if (rand.nextFloat() <= 0.05) {
            school.add(new GUITrainer("Chandler", 'M', 29));
        }

        // Runs a day at school.
        school.aDayAtSchool();

        // Might remove a spare teacher.
        ArrayList<Instructor> toRemoveInstructors = new ArrayList<>();

        for (Instructor instructor : school.getInstructors()) {
            if (instructor.getAssignedCourse() == null && rand.nextFloat() <= 0.2) {
                toRemoveInstructors.add(instructor);
                break;
            }
        }
        school.removeInstructors(toRemoveInstructors);

        // Might remove a student.
        ArrayList<Student> toRemoveStudents = new ArrayList<>();

        for (Student student : school.getStudents()) {
            boolean studying = false;

            // Removes any graduates that have a certificate in every course.
            if (student.getCertificates().size() == school.getSubjects().size() && school.getSubjects().size() > 0) {
                school.add(new Graduate(student.getName(), student.getGender(), student.getAge()));
                toRemoveStudents.add(student);
                break;
            }

            // Checks if the student is taking a course.
            for (Course course : school.getCourses()) {
                if (Arrays.asList(course.getStudents()).contains(student)) {
                    studying = true;
                }
            }

            // Might remove a spare student.
           if (!studying && rand.nextFloat() <= 0.05) {
                toRemoveStudents.add(student);
                break;
           }
        }
        school.removeStudents(toRemoveStudents);
    }

    // Runs the simulation for a given number of days.
    public void run(int days) {
        for (int i = 1; i <= days; i++) {
            run();

            System.out.println("Day " + i + ":");

            // Prints information about courses.
            for (Course course : school.getCourses()) {
                // Converts the ArrayList to show the student names
                ArrayList<String> students = new ArrayList<>();
                for (Student student : course.getStudents()) {
                    students.add(student.getName());
                }
                System.out.println("Course (" + course.getSubject().getDescription() + ") : " + course.getStatus() + " " + students);
            }

            // Prints information about students.
            for (Student student : school.getStudents()) {
                try {
                    System.out.println("Student (" + student.getName() + ") : " + student.getCertificates() + " " + student.getEnrolledCourse().getSubject().getDescription());
                } catch (NullPointerException e) {
                    System.out.println("Student (" + student.getName() + ") : " + student.getCertificates() + " No course");
                }
            }

            // Prints information about instructors.
            for (Instructor instructor : school.getInstructors()) {
                try {
                    System.out.println("Instructor (" + instructor.getName() + ") : " + instructor.getAssignedCourse().getSubject().getDescription());
                } catch (NullPointerException e) {
                    System.out.println("Instructor (" + instructor.getName() + ") : No course");
                }
            }

            updateGraduations(i);

            // Prints the status of the school and pauses for a second.
            try {
                System.out.println(this.school.toString());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    // Creates a random student.
    public Student createStudent() {
        StringBuilder name = new StringBuilder();
        char[] genders = {'M', 'F'};
        Random rand = new Random();

        name.append((char) (rand.nextInt(25) + 66));
        for (int i = 0; i < 4; i++) {
            name.append((char) (rand.nextInt(25) + 98));
        }

        return new Student(name.toString(), genders[rand.nextInt(2)], rand.nextInt(5) + 18);
    }

    // Sets the day of graduation for new graduates.
    public void updateGraduations(int day) {
        for (Graduate graduate : school.getGraduates()) {
            if (graduate.getGraduationDay() == 0) {
                graduate.setGraduationDay(day);
            }
        }
    }

    // Gets the initial configuration file.
    public static Administrator loadConfig(String fileName) throws FileNotFoundException {
        try {
            // Creates a reader to read the file which instantiates the administrator.
            BufferedReader fileReader = new BufferedReader(new FileReader(fileName));
            Administrator admin = new Administrator(getValue(fileReader.readLine()));
            String thisLine = fileReader.readLine();

            // Reads the rest of the file.
            while (thisLine != null) {
                String object = getObject(thisLine);
                String[] split = getValue(thisLine).split(",");

                // Fills the school.
                switch (object) {
                    // Adds a subject to the school.
                    case "subject":
                        Subject subject = new Subject(Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
                        subject.setDescription(split[0]);
                        admin.school.add(subject);
                        break;
                    // Adds a student to the school.
                    case "student":
                        Student student = new Student(split[0], split[1].charAt(0), Integer.parseInt(split[2]));
                        admin.school.add(student);
                        break;
                    // Adds a teacher to the school.
                    case "Teacher":
                        Teacher teacher = new Teacher(split[0], split[1].charAt(0), Integer.parseInt(split[2]));
                        admin.school.add(teacher);
                        break;
                    // Adds a demonstrator to the school.
                    case "Demonstrator":
                        Demonstrator demonstrator = new Demonstrator(split[0], split[1].charAt(0), Integer.parseInt(split[2]));
                        admin.school.add(demonstrator);
                        break;
                    // Adds a guiTrainer to the school.
                    case "GUITrainer":
                        GUITrainer guiTrainer = new GUITrainer(split[0], split[1].charAt(0), Integer.parseInt(split[2]));
                        admin.school.add(guiTrainer);
                        break;
                    // Adds a ooTrainer to the school.
                    case "OOTrainer":
                        OOTrainer ooTrainer = new OOTrainer(split[0], split[1].charAt(0), Integer.parseInt(split[2]));
                        admin.school.add(ooTrainer);
                        break;
                }
                thisLine = fileReader.readLine();
            }
            // Closes the file and returns the created admin.
            fileReader.close();
            return admin;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    // Reads the config file to return the value after the colon.
    public static String getValue(String line) {
        return line.substring(line.indexOf(':') + 1);
    }

    // Reads the config file to return the object before the colon.
    public static String getObject(String line) {
        return line.substring(0, line.indexOf(':'));
    }

    // Main method to run the simulation.
    public static void main(String[] args) {
        // Creates a scanner to input the school details.
        Scanner userInput = new Scanner(System.in);

        // Creates the administrator of the school and loads the initial configuration file.
        System.out.println("Enter in the name of the file that contains the initial school configuration.");
        try {
            Administrator admin = loadConfig(userInput.nextLine() + ".txt");

            // Runs the school for a given number of days.
            System.out.println("How long do you want to run the school for.");
            int daysToRun = userInput.nextInt();
            if (daysToRun > 0) {
                admin.run(daysToRun);
                System.out.println("Simulation Over");
            }
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }
}
