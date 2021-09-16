

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import exceptions.DataException;
import model.Student;
import services.SheetsServices;

public class Index {
    private static Integer NUMBER_TOTAL_CLASSES = 60;

    private static void processData(){
        try {
            List<List<Object>> values=SheetsServices.getData();
            if (values == null || values.isEmpty()) {
                System.out.println("no data found");
            } else {
                System.out.println("Dados lidos com sucesso");
                System.out.println("Montando turma");
                List<Student> students = new ArrayList<Student>();

                values.parallelStream().forEach(row->{
                    students.add(new Student(Integer.parseInt((String)row.get(0)),(String) row.get(1),Integer.parseInt((String)row.get(2)),Integer.parseInt((String)row.get(3)),Integer.parseInt((String)row.get(4)),Integer.parseInt((String)row.get(5))));
                });
    
                System.out.println("Turma montada");
                System.out.println("Iniciando Processamento de aprovação");
                students.parallelStream().forEach(student->{
                    float percentageOfAbsences=student.calculateAbsences(NUMBER_TOTAL_CLASSES);
                    if(percentageOfAbsences>25){
                        try {
                            student.setSituation("Reprovado por Falta");
                            student.updateStudentStatus();
                        } catch (DataException e) {
                            System.out.print("\n an error occurred while modifying a students satus modification data: {situation:'Reprovado por falta'}");
                            System.out.printf("\n student data:{studentRegistration:%s,studentName:%s}",student.getRegistration(),student.getStudanteName());
                            e.printStackTrace();
                        }
                    }else{
                        int studentAVG=student.averageCalculation();
                        if(studentAVG>=70){
                            try {
                                student.setSituation("Aprovado");
                                student.updateStudentStatus();
                            } catch (DataException e) {
                                System.out.print("\n an error occurred while modifying a students satus modification data: {situation:'Aprovado'}");
                                System.out.printf("\n student data:{studentRegistration:%s,studentName:%s}",student.getRegistration(),student.getStudanteName());
                                e.printStackTrace();
                            }
                        }else if(studentAVG<50){
                            try {
                                student.setSituation("Reprovado Por Nota");
                                student.updateStudentStatus();
                            } catch (DataException e) {
                                System.out.print("\n an error occurred while modifying a students satus modification data: {situation:'Reprovado Por Nota'}");
                                System.out.printf("\n student data:{studentRegistration:%s,studentName:%s}",student.getRegistration(),student.getStudanteName());
                                e.printStackTrace();
                            }
                        }
                        else{
                            try {
                                student.setSituation("Exame Final");
                                student.calculateGrade_for_final_approval();
                                student.updateStudentStatus();
                            } catch (DataException e) {
                                System.out.print("\n an error occurred while modifying a students satus modification data: {situation:'Exame Final'}");
                                System.out.printf("\n student data:{studentRegistration:%s,studentName:%s}",student.getRegistration(),student.getStudanteName());
                            e.printStackTrace();
                            }                        
                        }
                    }
    
                });
                System.out.println("Iniciando Processo de aprovação concluido, por favor dirija-se até a pnalinha conferir o resultado");

            }
        }catch(Exception e){
            System.out.printf("an error occurred while running the program, error message: %s", e);
            e.printStackTrace();
        }finally {
            System.out.printf("program execution was interrupted");
        }
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        System.out.println("Iniciando processamento");
        processData();
        System.out.println("O processamento terminou");
    }

}
