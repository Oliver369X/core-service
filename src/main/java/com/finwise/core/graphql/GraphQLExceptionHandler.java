package com.finwise.core.graphql;

import com.finwise.core.domain.exception.ResourceNotFoundException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment environment) {
        if (ex instanceof ResourceNotFoundException notFound) {
            return GraphqlErrorBuilder.newError(environment)
                    .message(notFound.getMessage())
                    .errorType(graphql.ErrorType.DataFetchingException)
                    .build();
        }
        return null;
    }
}
