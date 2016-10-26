package faq.resources;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("questoes")
public class QuestaoResource {

    @GET
    @Timed
    public String hello() {
        return "Olá, como vai?";
    }

}
