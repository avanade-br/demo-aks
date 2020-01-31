package demo;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/")
public class DemoController
{
    ArrayList<UUID> repository = new ArrayList<UUID>(65536);

    Boolean isOffline = false;


    @GetMapping(path="/clientIP", produces = "plain/text")
    public ResponseEntity<String> clientIP(HttpServletRequest request)
    {
        return new ResponseEntity<>(request.getRemoteAddr(), HttpStatus.OK);
    }

    @GetMapping(path="/health/isReady", produces = "plain/text")
    public ResponseEntity<String> readinessProbe(@RequestParam Optional<Boolean> offline)
    {
        // Ativa/Desativa o flag "forced-offline", caso tenha sido requisitado
        if ( offline.isPresent() )
            this.isOffline = offline.get();

        // Obtém o limite de requests simultâneos (parametrizado em variável de ambiente)
        int activeRequestsLimit = Helpers.getEnvVar("ACTIVE_REQUESTS_LIMIT", 999);

        // Obtém a quantidade atual de requests ativos (i.e. em execução no momento)
        int activeRequestCount = RequestControl.getActiveRequestCount();
        
        // Retorna erro HTTP 503 se quantidade de requests simultâneos supera o limite definido,
        if ( activeRequestCount > activeRequestsLimit )
        {
            System.out.printf("[%s] The readiness probe has failed due to excessive simultaneous requests (Active Requests: %d)\n", 
                                new Date(), activeRequestCount);
            return new ResponseEntity<>("Not Ready", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Retorna erro HTTP 503 se a probe liveness falhou
        if ( livenessProbe().getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE )
        {
            System.out.printf("[%s] The readiness probe has failed because this pod is going to be evicted\n", new Date());
            return new ResponseEntity<>("Not Ready", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Retorna erro HTTP 503 se a flag forced-offline tiver sido ativada
        if ( this.isOffline )
        {
            System.out.printf("[%s] The readiness probe has failed because the flag offline is enabled\n", new Date());
            return new ResponseEntity<>("Not Ready", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Retorna status HTTP 200 OK
        return new ResponseEntity<>("Ready", HttpStatus.OK);
	}
	
    @GetMapping(path="/health/isAlive", produces = "application/json")
    public ResponseEntity<AppStats> livenessProbe() 
    {
        // Obtém o threshold de memória em uso (percentual) que implica no restart do POD
        int memoryUsagePercentThreshold = Helpers.getEnvVar("MEMORY_USAGE_PERCENT_THRESHOLD", 90);

        // Obtém métricas de utilização da aplicação
        AppStats appStats = AppStats.create();

        // Retorna erro HTTP 503 se a aplicação está usando mais memória do que o threshold configurado
        if ( appStats.getPercentUsedMemory() > memoryUsagePercentThreshold )
        {
            System.out.printf("[%s] The liveness probe has failed due to excessive memory consumption (Used memory: %f%%)\n", 
                             new Date(), appStats.getPercentUsedMemory());
            return new ResponseEntity<>(appStats, HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Retorna status HTTP 200 OK
        return new ResponseEntity<>(appStats, HttpStatus.OK);
	}

    @PostMapping(path = "/transaction", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TransactionResponse> runTransaction(@Valid @RequestBody TransactionRequest request) 
    {
        RequestControl.startRequest();

        try 
        {
            int size = (int) Math.pow(2.0, (double) request.getBatchSize());

            repository.ensureCapacity(repository.size() + size);

            for ( int i = 0; i < size; i++ ) 
            {
                repository.add(UUID.randomUUID());
            }

            TransactionResponse response = TransactionResponse.Create(repository.size());
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);

        } 
        finally 
        {
            RequestControl.endRequest();
        }
    }
}