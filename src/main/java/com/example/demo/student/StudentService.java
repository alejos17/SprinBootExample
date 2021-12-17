package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    public void addNewStudent(Student student) {
        //Verifica primero si el email dado para el nuevo student ya esta en la base de datos
        //si es asi, entonces genera error y no graba...
        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()){
            throw new IllegalStateException("email taken");
        }
        studentRepository.save(student);
        System.out.println(student);
    }

    public void deleteStudent(Long studentId) {
        boolean exists = studentRepository.existsById(studentId);
        if (!exists){
            throw new IllegalStateException("student with id "+studentId+" does not exists");
        }
        studentRepository.deleteById(studentId);
    }

    @Transactional  //Significa que tiene valores pendientes que pueden o no ser guardados
    public void updateStudent(Long studentId, String name, String email) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalStateException("student with id "+studentId+" does not exists"));

        // Condicion que valida que el nombre no sea nulo, que tenga mas de 1 caracter y que no sea el mismo que esta
        //guardado.
        if (name != null && name.length() > 0 && !Objects.equals(student.getName(), name)){
            student.setName(name);  //Si efectivamente es diferente se setea el nuevo nombre
        }

        //La misma validacion con el correo
        if (email != null && email.length() > 0 && !Objects.equals(student.getEmail(), email)){
            //Se revalida que el mail no este en base de datos con otro nombre o en otro registro.
            Optional<Student> studentOptional = studentRepository.findStudentByEmail(email);
            if (studentOptional.isPresent()){
                throw new IllegalStateException("email taken");
            }
            student.setEmail(email);
        }
    }
}
