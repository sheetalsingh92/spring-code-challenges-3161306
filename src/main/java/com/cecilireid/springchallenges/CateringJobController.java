package com.cecilireid.springchallenges;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("cateringJobs")
public class CateringJobController {
    private static final String IMAGE_API = "https://foodish-api.herokuapp.com";
    private final CateringJobRepository cateringJobRepository;
    WebClient client;

    public CateringJobController(CateringJobRepository cateringJobRepository, WebClient.Builder webClientBuilder) {
        this.cateringJobRepository = cateringJobRepository;
    }

    @GetMapping
    @ResponseBody
    public List<CateringJob> getCateringJobs() {
        return cateringJobRepository.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public CateringJob getCateringJobById(@PathVariable Long id) {
        if (cateringJobRepository.existsById(id)) {
            return cateringJobRepository.findById(id).get();
        } else {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findbyStatus")
    public List<CateringJob> getCateringJobsByStatus(@RequestParam Status status) {
       return cateringJobRepository.findByStatus(status);
    }

    @PostMapping("/create")
    @ResponseBody
    public CateringJob createCateringJob(@RequestBody CateringJob job) {
        return cateringJobRepository.save(job);
    }

    @PutMapping("update/{id}")
    public CateringJob updateCateringJob(@RequestBody CateringJob cateringJob, @PathVariable Long id) {
        if(cateringJobRepository.existsById(id))
        {
            cateringJob.setId(id);
            return cateringJobRepository.save(cateringJob);
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

    }

    @PatchMapping("/{id}")
    @ResponseBody
    public CateringJob patchCateringJob(@PathVariable Long id, @RequestBody JsonNode json) {
        Optional<CateringJob> optionalJob =  cateringJobRepository.findById(id);
        if(optionalJob.isPresent()){
            CateringJob job = optionalJob.get();
            JsonNode menu = json.get("menu");
            if(menu != null)
            {
                job.setMenu(menu.asText());
                return cateringJobRepository.save(job);
            }
            else
            {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            }
        }
        else
        {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }

    public Mono<String> getSurpriseImage() {
        return null;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String handleClientException(){
        return "Not Found: Please try again";
    }
}
