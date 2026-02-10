public interface CustomerLookup {
  List<CustomerDto> getCustomersByIds(List<CustomerId> customerIds);
}
