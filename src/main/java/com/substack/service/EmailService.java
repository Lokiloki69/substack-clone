package com.substack.service;

import com.substack.model.Post;
import com.substack.model.*;
import com.substack.model.Subscription;
import com.substack.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SubscriptionRepository subscriptionRepository;

    public void sendEmailToSubscribers(Post post, Publication publication) {
        List<Subscription> subscribers = subscriptionRepository
                .findByAuthorIdAndActive(publication.getOwner().getId(), true);

        for (Subscription subscription : subscribers) {
            sendEmail(
                    subscription.getSubscriber().getEmail(),
                    post.getTitle(),
                    buildEmailContent(post)
            );
        }
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("noreply@substack.local");
            mailSender.send(message);
        } catch (Exception e) {
            // Log error
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    private String buildEmailContent(Post post) {
        return "Dear Subscriber,\n\n" +
                "New post: " + post.getTitle() + "\n\n" +
                post.getContent() + "\n\n" +
                "Read on Substack: http://localhost:8080/posts/" + post.getId();
    }
}