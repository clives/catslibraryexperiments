package monocle

import monocle.function.Cons.headOption

case class Street(number: Int, name: String)
case class Address(city: String, street: Street)
case class Company(name: String, address: Address)
case class Employee(name: String, company: Company)

trait SimpleMonocleUseCase {
  import monocle.Lens
  import monocle.macros.GenLens

  val company   : Lens[Employee, Company] = GenLens[Employee](_.company)
  val companyName : Lens[Company, String] = GenLens[Company](_.name)
  val address   : Lens[Company , Address] = GenLens[Company](_.address)
  val street    : Lens[Address , Street]  = GenLens[Address](_.street)
  val streetName: Lens[Street  , String]  = GenLens[Street](_.name)

  company composeLens address composeLens street composeLens streetName


  val updateCompanyName = company composeLens companyName
  val updateCompanyNameWithHeadOption = company composeLens companyName composeOptional headOption
}


object AppSimpleUseCase extends SimpleMonocleUseCase with App {

  val employee1 = Employee("john", Company("aWeSome inc", Address("london", Street(23, "high street"))))
  val employee1Updated= updateCompanyName.modify(_.toLowerCase)(employee1)

  println( s"original: $employee1, updated:$employee1Updated")


  println("Test modify only the first element for String => first caracter using headOption:")
  val employeeWithoutCompany = Employee("john", Company("lowercasename", Address("london", Street(23, "high street"))))
  val resultWithHeadOption=(company composeLens companyName composeOptional headOption).modify(_.toUpper)(employeeWithoutCompany)
  println( s"Test using headOption original: $employeeWithoutCompany, updated:$resultWithHeadOption")

}
