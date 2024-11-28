package com.springBoot.journalProject.service;

import com.springBoot.journalProject.entity.PostEntry;
import com.springBoot.journalProject.entity.User;
import com.springBoot.journalProject.repository.PostEntryRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EntryService
{
    @Autowired
    private PostEntryRepo postEntryRepo;
    @Autowired
    private UserService uservice;


    public void saveEntry(PostEntry postEntry, String userName)
    {
        try
        {
            User user = uservice.findByUserName(userName);
            postEntry.setDate(LocalDateTime.now());
            PostEntry saved = postEntryRepo.save(postEntry);
            user.getEntry().add(saved);
            uservice.saveEntry(user);
        }
        catch (Exception e)
        {
            System.out.println(e);
            throw new RuntimeException("An error occurred while saving the entry ",e);
        }
    }


    public void saveEntry(PostEntry postEntry)
    {
        postEntryRepo.save(postEntry);
    }


    public List<PostEntry> getAll()
    {
        return postEntryRepo.findAll();
    }

    public Optional<PostEntry> findById(ObjectId id)
    {
        return postEntryRepo.findById(id);
    }

    public void deleteById(ObjectId id, String userName)
    {
        User user = uservice.findByUserName(userName);
        user.getEntry().removeIf(x -> x.getId().equals(id));
        uservice.saveEntry(user);
        postEntryRepo.deleteById(id);
    }


}
