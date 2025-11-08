package com.substack.service;

import com.substack.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailService emailService;

    public void notifyNewPost(Post post, Publication publication) {
//        emailService.sendEmailToSubscribers(post, publication);
    }

    public void notifyNewSubscriber(User subscriber, Publication publication) {
        // Send notification to publication owner
        emailService.sendEmail(
                publication.getOwner().getEmail(),
                "New Subscriber",
                subscriber.getName() + " subscribed to " + publication.getName()
        );
    }


    public void notifyNewComment(Comment comment) {
        // Send notification to post author
//        emailService.sendEmail(
//                comment.getPost().getAuthor().getEmail(),
//                "New Comment on " + comment.getPost().getTitle(),
//                comment.getName() + " commented: " + comment.getComment()
//        );
    }
}
