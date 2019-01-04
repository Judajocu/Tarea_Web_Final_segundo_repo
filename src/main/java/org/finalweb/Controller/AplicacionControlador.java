package org.finalweb.Controller;

import org.finalweb.Entity.Receipt;
import org.finalweb.Service.CRUD.ServicioLector;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class AplicacionControlador {
    ServicioLector RDS;

    @RequestMapping(value ="/greeting", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody  List<Receipt> greeting(@RequestParam(value="name", defaultValue="World") String name) {

        List<Receipt> test= RDS.findAllRegisteredTransactions();
        if (RDS.findAllRegisteredTransactions() == null)
        {
            test= new ArrayList<>();
            return test;
        }



        return test;

    }
}
