package demo;

import java.util.UUID;
import java.util.ArrayList;
import java.util.Date;

import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/")
public class DemoController
{
    ArrayList<UUID> repository = new ArrayList<UUID>(65536);

    private int getEnvVar(String varName, int defaultValue)
    {
        try
        {
            return Integer.parseInt(System.getenv().get(varName));
        }
        catch(NumberFormatException e)
        {
            return defaultValue;
        }

    }

    @GetMapping(path="/health/isReady", produces = "plain/text")
    public ResponseEntity<String> readinessProbe() 
    {
        // Obtém o limite de requests simultâneos (parametrizado em variável de ambiente)
        int activeRequestsLimit = getEnvVar("ACTIVE_REQUESTS_LIMIT", 999);

        // Obtém a quantidade atual de requests ativos (i.e. em execução no momento)
        int activeRequestCount = RequestControl.getActiveRequestCount();
        
        // Retorna erro HTTP 503 se quantidade de requests simultâneos supera o limite definido,
        if ( activeRequestCount > activeRequestsLimit )
        {
            System.out.printf("[%s] The readiness probe has failed due to excessive simultaneous requests (Active Requests: %d)\n", new Date(), activeRequestCount);
            return new ResponseEntity<>("Not Ready", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Retorna erro HTTP 503 se a probe liveness falhou
        if ( livenessProbe().getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE )
        {
            System.out.printf("[%s] The readiness probe has failed because this pod is going to be evicted\n", new Date());
            return new ResponseEntity<>("Not Ready", HttpStatus.SERVICE_UNAVAILABLE);
        }

        // Retorna status HTTP 200 OK
        return new ResponseEntity<>("Ready", HttpStatus.OK);
	}
	
    @GetMapping(path="/health/isAlive", produces = "application/json")
    public ResponseEntity<AppStats> livenessProbe() 
    {
        // Obtém o threshold de memória em uso (percentual) que implica no restart do POD
        int memoryUsagePercentThreshold = getEnvVar("MEMORY_USAGE_PERCENT_THRESHOLD", 90);

        // Obtém métricas de utilização da aplicação
        AppStats appStats = AppStats.create();

        // Retorna erro HTTP 503 se a aplicação está usando mais memória do que o threshold configurado, 
        // ou status HTTP 200 OK caso contrário
        if ( appStats.getPercentUsedMemory() > memoryUsagePercentThreshold )
        {
            System.out.printf("[%s] The liveness probe has failed due to excessive memory consumption (Used memory: %f%%)\n", new Date(), appStats.getPercentUsedMemory());
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