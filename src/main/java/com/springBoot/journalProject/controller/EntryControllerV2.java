package com.springBoot.journalProject.controller;

import com.springBoot.journalProject.entity.PostEntry;
import com.springBoot.journalProject.entity.User;
import com.springBoot.journalProject.service.EntryService;
import com.springBoot.journalProject.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/journal")
public class EntryControllerV2
{


    private final EntryService es;
    private final UserService uservice;

    @GetMapping
    public ResponseEntity<?> getallEntriesofUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = uservice.findByUserName(userName);
        List<PostEntry> all = user.getEntry() ;
        if (all != null && !all.isEmpty())
        {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PostMapping
    public ResponseEntity<PostEntry> cerate(@RequestBody PostEntry entry)
    {
        try
        {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            es.saveEntry(entry, userName);
            return new ResponseEntity<>(entry, HttpStatus.CREATED);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
    }


    @GetMapping("id/{id}")
    public ResponseEntity<PostEntry> getdata(@PathVariable ObjectId id)
    {
        Optional<PostEntry> allEntry = es.findById(id);
        if (allEntry.isPresent())
        {
            return new ResponseEntity<>(allEntry.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @DeleteMapping("id/{id}")
    public ResponseEntity<?> deletedata(@PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        boolean removed = es.deleteById(id, userName);
        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("id/{userName}/{id}")
    public ResponseEntity<?> update(@PathVariable ObjectId id, @RequestBody PostEntry update, @PathVariable String userName)
    {
        PostEntry old = es.findById(id).orElse(null);
        if (old != null)
        {
            old.setTitle(update.getTitle() != null && !update.getTitle().equals("") ? update.getTitle() : old.getTitle());
            old.setContent(update.getContent() != null && !update.getContent().equals("") ? update.getContent() : old.getContent());
            es.saveEntry(old);
            return new ResponseEntity<>(old, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
