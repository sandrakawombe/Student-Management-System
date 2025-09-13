// controller/EnrollmentController.java
package com.davidsdk.studentmgmt.enrollment.controller;
import com.davidsdk.studentmgmt.enrollment.domain.Enrollment;
import com.davidsdk.studentmgmt.enrollment.dto.*;
import com.davidsdk.studentmgmt.enrollment.service.EnrollmentService;
import jakarta.servlet.http.HttpServletRequest; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/enrollments") @RequiredArgsConstructor
public class EnrollmentController {
  private final EnrollmentService service;

  @PostMapping @ResponseStatus(HttpStatus.CREATED)
  public Enrollment create(@Valid @RequestBody EnrollRequest req, HttpServletRequest http){
    String bearer = http.getHeader(HttpHeaders.AUTHORIZATION);
    return service.enroll(req.studentId(), req.courseId(), bearer);
  }

  // add list endpoint using repo in service if you prefer strict layering; simplified here:
  @GetMapping public List<Enrollment> byStudent(@RequestParam Long studentId){ throw new UnsupportedOperationException("Implement in service/repo"); }

  @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@PathVariable Long id){ service.cancel(id); }
}
