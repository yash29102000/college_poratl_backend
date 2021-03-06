package demo.login.controllers;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import demo.login.data.Job;
import demo.login.data.User;
import demo.login.payload.response.JobResponse;
import demo.login.repository.BlobStorageRepository;
import demo.login.repository.JobRepository;
import demo.login.repository.UserRepository;

@RestController
@RequestMapping(path = "/api")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BlobStorageRepository blobStorageRepository;

    @Autowired
    CommonService commonService;

    @PostMapping("/job")
    public ResponseEntity<String> JobUpload(@RequestParam("userId") Long userId,
            @RequestParam("jobContent") String jobContent,
            @RequestParam("companyName") String companyName,
            @RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            String fileType = file == null ? null : file.getContentType();
            String fileUrl = blobStorageRepository.uploadFile("postdocuments", file);
            User user = userRepository.findById(userId).get();
            Job job = new Job(user, jobContent, companyName, fileType, fileUrl);
            jobRepository.save(job);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException | InvalidKeyException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jobs")
    public List<JobResponse> getJobs() {
        List<Job> jobs = jobRepository.findAll();
        jobs.sort((a, b) -> b.getJobDate().compareTo(a.getJobDate()));
        return jobs.stream().map(commonService::mapJobToJobResponse).collect(Collectors.toList());
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<JobResponse> getJob(@PathVariable Long id) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            return ResponseEntity.ok(commonService.mapJobToJobResponse(jobOptional.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        try {
            jobRepository.deleteById(id);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/users/{id}/jobs")
    public List<JobResponse> getPostsByUserId(@PathVariable(name = "id") Long id) {
        User user = userRepository.findById(id).get();
        List<Job> jobs = jobRepository.findAllByUser(user);
        return jobs.stream().map(commonService::mapJobToJobResponse).collect(Collectors.toList());
    }
}
