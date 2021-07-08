package com.vic.io.covidvaccination.Service;

import com.vic.io.covidvaccination.Model.Centers;
import com.vic.io.covidvaccination.Model.SessionList;
import com.vic.io.covidvaccination.Model.User;
import com.vic.io.covidvaccination.Notification.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// TODO: 18/06/21 fix issues
// TODO: 23/06/21 lot of clean up
// TODO: 26/06/21 cache the repo

@Service
public class CenterCheck {
    private final Notify notify;
    private final GetAvailability getAvailability;
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime date = now.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));

    DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private static final Logger log= LoggerFactory.getLogger(CenterCheck.class);

    @Autowired
    public CenterCheck(Notify notify, GetAvailability getAvailability) {
        this.notify = notify;
        this.getAvailability = getAvailability;
    }

    public void snd(User user){
       // if (user.isEnable()) {
            notify.SendSms(user);
       // }
        return;
    }

    public List<String> setDate(User user){
        List<String> dates=new ArrayList<>();
        for (Centers centers1: user.getAvailableCenters()){
            for (SessionList sessionList: centers1.getSessions()){
                dates.add(sessionList.getDate());
            }
        }
        System.out.println(dates.stream().distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));
        return (dates.stream().distinct().sorted(Comparator.reverseOrder()).collect(Collectors.toList()));

    }

    public List<String> extractName(List<Centers> centers){
        List<String> centreNames=new ArrayList<>();
        for (Centers centers1:centers){
            centreNames.add(centers1.getName());
        }
        return centreNames;
    }

    public User setData(User user) {
        log.info(user.getUserName());

        if (!this.getAvailability.getCenters(user).isEmpty()) {
            user.setAvailableCenters(this.getAvailability.getCenters(user));
            List<String> date = setDate(user);

            user.setFrom(date.get(date.size() - 1));
            user.setTo(date.get(0));
            user.setEnable(true);
        }else {
            log.info(user.getUserName()+" has no avilable centers");

            user.setAvailableCenters(new ArrayList<>());
            user.setFrom(date.format(dataFormatter));
            user.setTo(date.format(dataFormatter));
        }
        return user;
    }


}
