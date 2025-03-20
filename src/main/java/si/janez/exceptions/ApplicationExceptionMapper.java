package si.janez.exceptions;

import io.quarkus.logging.Log;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import si.janez.api.model.Error;

@Provider
public class ApplicationExceptionMapper implements ExceptionMapper<ApplicationException> {
    @Override
    public Response toResponse(ApplicationException exception) {
        Log.log(exception.logLevel, "Exception : ", exception);

        var error = new Error()
                .code(exception.httpCode)
                .message(exception.getMessage());

        return Response.status(exception.httpCode)
                .entity(error)
                .build();
    }
}
