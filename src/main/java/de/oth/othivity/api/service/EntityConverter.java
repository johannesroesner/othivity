package de.oth.othivity.api.service;

import de.oth.othivity.api.dto.ActivityApiDto;
import de.oth.othivity.api.dto.ProfileApiDto;
import de.oth.othivity.dto.ActivityDto;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.model.helper.Address;
import de.oth.othivity.model.helper.Email;
import de.oth.othivity.model.helper.Image;
import de.oth.othivity.model.helper.Phone;
import de.oth.othivity.service.ActivityService;
import de.oth.othivity.service.ClubService;
import de.oth.othivity.api.dto.ProfileApiDto;
import de.oth.othivity.dto.ProfileDto;
import de.oth.othivity.dto.RegisterDto;
import de.oth.othivity.model.main.Profile;
import de.oth.othivity.model.helper.Phone;
import de.oth.othivity.model.helper.Email;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import de.oth.othivity.model.main.Activity;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EntityConverter {

    private final ClubService clubService;
    private final ActivityService activityService;


    private String safe(Object value) {
        return value == null ? null : value.toString();
    }

    private <T> String[] safeArray(Collection<T> collection, Function<T, String> mapper) {
        return collection == null ? new String[0] : collection.stream().map(mapper).toArray(String[]::new);
    }

    public ActivityApiDto ActivityToApiDto(Activity activity) {

        ActivityApiDto response = new ActivityApiDto();

        response.setId(safe(activity.getId()));
        response.setTitle(activity.getTitle());
        response.setDescription(activity.getDescription());
        response.setDate(safe(activity.getDate()));
        response.setGroupSize(activity.getGroupSize());

        response.setTags(safeArray(activity.getTags(), Enum::name));
        response.setTakePart(safeArray(activity.getTakePart(), p -> safe(p.getId())));

        response.setImageUrl(
                activity.getImage() == null ? null : activity.getImage().getUrl()
        );

        response.setLanguage(
                activity.getLanguage() == null ? null : activity.getLanguage().getName()
        );

        response.setOrganizerId(
                activity.getOrganizer() == null ? null : safe(activity.getOrganizer().getId())
        );

        response.setStartedBy(
                activity.getStartedBy() == null ? null : safe(activity.getStartedBy().getId())
        );

        if (activity.getAddress() == null) {
            response.setAddition(null);
            response.setStreet(null);
            response.setHouseNumber(null);
            response.setCity(null);
            response.setPostalCode(null);
            response.setCountry(null);
            response.setLatitude(null);
            response.setLongitude(null);
        } else {
            Address address = activity.getAddress();

            response.setAddition(safe(address.getAddition()));
            response.setStreet(safe(address.getStreet()));
            response.setHouseNumber(safe(address.getHouseNumber()));
            response.setCity(safe(address.getCity()));
            response.setPostalCode(safe(address.getPostalCode()));
            response.setCountry(safe(address.getCountry()));
            response.setLatitude(safe(address.getLatitude()));
            response.setLongitude(safe(address.getLongitude()));
        }

        return response;
    }

    public ActivityDto ApiDtoToActivityDto(ActivityApiDto request) {
        ActivityDto activity = new ActivityDto();

        if(request.getTitle() != null) activity.setTitle(request.getTitle());
        else throw new IllegalArgumentException("title is null");

        if(request.getDescription() != null) activity.setDescription(request.getDescription());
        else throw new IllegalArgumentException("description is null");

        if(request.getLanguage() != null) {
            try {
                activity.setLanguage(Enum.valueOf(de.oth.othivity.model.enumeration.Language.class, request.getLanguage()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("language is invalid");
            }
        } else throw new IllegalArgumentException("language is null");

        if(request.getGroupSize() >= 2) activity.setGroupSize(request.getGroupSize());
        else throw new IllegalArgumentException("groupSize is invalid (must be >= 2)");

        if(request.getDate() != null) {
            try {
                activity.setDate(java.time.LocalDateTime.parse(request.getDate()));
            } catch (Exception e) {
                throw new IllegalArgumentException("date is invalid");
            }
        } else throw new IllegalArgumentException("date is null");

        try {
            activity.setTags(Arrays.stream(request.getTags()).map(tag -> Enum.valueOf(de.oth.othivity.model.enumeration.Tag.class, tag)).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new IllegalArgumentException("tags are invalid");
        }

        if(request.getOrganizerId() != null && clubService.getClubById(UUID.fromString(request.getOrganizerId())) == null) throw  new IllegalArgumentException("organizer id is invalid");
        else {
            if(request.getOrganizerId() != null) activity.setOrganizer(clubService.getClubById(UUID.fromString(request.getOrganizerId())));
            else activity.setOrganizer(null);
        }

        if(request.getStreet() != null && request.getHouseNumber() != null && request.getCity() != null && request.getPostalCode() != null) {

            Address address = new Address();
            address.setAddition(request.getAddition());
            address.setStreet(request.getStreet());
            address.setHouseNumber(request.getHouseNumber());
            address.setCity(request.getCity());
            address.setPostalCode(request.getPostalCode());
            address.setCountry(request.getCountry());
            activity.setAddress(address);
        } else throw new IllegalArgumentException("address is invalid");

        if(request.getImageUrl() != null){
            Image image = new Image();
            image.setPublicId("not stored in claudinary");
            image.setUrl(request.getImageUrl());
            activity.setImage(image);
        } else  throw new IllegalArgumentException("imageUrl is null");

        return activity;
    }

    public ProfileApiDto ProfileToApiDto(Profile profile) {
        ProfileApiDto response = new ProfileApiDto();
        
        response.setId(safe(profile.getId()));
        response.setFirstName(safe(profile.getFirstName()));
        response.setLastName(safe(profile.getLastName()));
        response.setAboutMe(safe(profile.getAboutMe()));
        response.setUsername(safe(profile.getUsername()));
        
        response.setLanguage(
            profile.getLanguage() != null ? profile.getLanguage().name() : null
        );

        response.setTheme(
            profile.getTheme() != null ? profile.getTheme().name() : null
        );
        
        response.setEmail(
            profile.getEmail() != null ? profile.getEmail().getAddress() : null
        );
        
        response.setPhone(
            profile.getPhone() != null ? profile.getPhone().getNumber() : null
        );
        
        response.setImageUrl(
            profile.getImage() != null ? profile.getImage().getUrl() : null
        );

        return response;
    }

    public ProfileDto ApiDtoToProfileDto(ProfileApiDto request) {
        ProfileDto profileDto = new ProfileDto();

        profileDto.setAboutMe(request.getAboutMe());

        if (request.getPhone() != null) {
            profileDto.setPhone(new Phone(request.getPhone()));
        }
        
        return profileDto;
    }

    public RegisterDto ApiDtoToRegisterDto(ProfileApiDto request) {
        RegisterDto registerDto = new RegisterDto();

        if (request.getFirstName() != null) registerDto.setFirstName(request.getFirstName());
        else throw new IllegalArgumentException("firstName is null");

        if (request.getLastName() != null) registerDto.setLastName(request.getLastName());
        else throw new IllegalArgumentException("lastName is null");

        if (request.getUsername() != null) registerDto.setUsername(request.getUsername().toLowerCase());
        else throw new IllegalArgumentException("username is null");

        if (request.getEmail() != null) registerDto.setEmail(request.getEmail().toLowerCase());
        else throw new IllegalArgumentException("email is null");

        if (request.getPassword() != null) registerDto.setPassword(request.getPassword());
        else throw new IllegalArgumentException("password is null");

        return registerDto;
    }

}
