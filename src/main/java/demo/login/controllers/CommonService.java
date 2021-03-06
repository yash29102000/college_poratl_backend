package demo.login.controllers;

import java.util.List;

import org.springframework.stereotype.Component;

import demo.login.data.Comment;
import demo.login.data.Job;
import demo.login.data.Post;
import demo.login.data.Report;
import demo.login.data.User;
import demo.login.payload.response.JobResponse;
import demo.login.payload.response.PostResponse;
import demo.login.payload.response.ReportResponse;
import demo.login.payload.response.UserResponse;
import demo.login.repository.ReportRepository;

@Component
public class CommonService {

    public UserResponse mapUserToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setDateOfBirth(user.getDateOfBirth());
        userResponse.setEnrollmentNo(user.getEnrollmentNo());
        userResponse.setFirstname(user.getFirstname());
        userResponse.setLastname(user.getLastname());
        userResponse.setBranch(user.getBranch());
        userResponse.setCourse(user.getCourse());
        userResponse.setPassoutYear(user.getPassoutYear());
        userResponse.setGender(user.getGender());
        userResponse.setPhoneNumber(user.getPhoneNumber());
        userResponse.setFileurl(user.getFileurl());
        return userResponse;
    }

    public PostResponse mapPostToPostResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setPostId(post.getPostId());
        postResponse.setContent(post.getContent());
        postResponse.setPostDate(post.getPostDate());
        postResponse.setUser(this.mapUserToUserResponse(post.getUser()));
        postResponse.setPostType(post.getPostType());
        postResponse.setReported(post.getReported());
        List<Comment> comments = post.getComments();
        comments.sort((a, b) -> a.getCommentDate().compareTo(b.getCommentDate()));
        postResponse.setComments(comments);
        postResponse.setFileUrl(post.getFileUrl());
        return postResponse;
    }

    public JobResponse mapJobToJobResponse(Job job) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.setJobId(job.getJobId());
        jobResponse.setContent(job.getContent());
        jobResponse.setJobDate(job.getJobDate());
        jobResponse.setUser(this.mapUserToUserResponse(job.getUser()));
        jobResponse.setCompanyName(job.getCompanyName());
        jobResponse.setReported(job.getReported());
        List<Comment> comments = job.getComments();
        comments.sort((a, b) -> a.getCommentDate().compareTo(b.getCommentDate()));
        jobResponse.setComments(comments);
        jobResponse.setFileUrl(job.getFileUrl());
        return jobResponse;
    }

    public ReportResponse mapReportToReportResponse(Report report) {
        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setReportId(report.getReportId());
        reportResponse.setMessage(report.getMessage());
        reportResponse.setUser(mapUserToUserResponse(report.getUser()));
        if (report.getPost() != null)
            reportResponse.setPost(mapPostToPostResponse(report.getPost()));
        if (report.getJob() != null)
            reportResponse.setJob(mapJobToJobResponse(report.getJob()));
        return reportResponse;
    }

}
