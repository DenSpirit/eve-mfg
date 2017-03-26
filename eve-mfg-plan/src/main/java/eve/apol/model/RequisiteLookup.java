package eve.apol.model;

import java.util.stream.Stream;

import eve.apol.entity.Requisite;

public interface RequisiteLookup {

    Stream<Requisite> getRequisites(Requisite order);

}
