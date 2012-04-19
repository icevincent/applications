package org.universAAL.ontology.nutrition;

import org.universAAL.middleware.owl.DataRepOntology;
import org.universAAL.middleware.owl.IntRestriction;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntClassInfoSetup;
import org.universAAL.middleware.owl.Ontology;
import org.universAAL.middleware.owl.TypeURI;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.service.owl.Service;
import org.universAAL.middleware.service.owl.ServiceBusOntology;
import org.universAAL.ontology.NutritionFactory;

/**
 * 
 * @author Hector Galan
 * 
 */
public final class NutritionOntology extends Ontology {

	private static NutritionFactory factory = new NutritionFactory();;

	public static final String NAMESPACE = "http://ontology.universAAL.org/Nutrition.owl#";

	public NutritionOntology() {
		super(NAMESPACE);
	}

	public void create() {
		Resource r = getInfo();
		r.setResourceComment("The ontology defining concepts about nutrition.");
		r.setResourceLabel("Lighting");

		addImport(DataRepOntology.NAMESPACE);
		addImport(ServiceBusOntology.NAMESPACE);

		OntClassInfoSetup oci;

		// load Enum. DayOfWeek
		oci = createNewAbstractOntClassInfo(DayOfWeek.MY_URI);
		oci.setResourceComment("Enumarion for days of week");
		oci.setResourceLabel("DayOfWeek");
		oci.toEnumeration(new ManagedIndividual[] { DayOfWeek.SUNDAY,
				DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
				DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY });

		// load Enum. MealCategory
		oci = createNewAbstractOntClassInfo(MealCategory.MY_URI);
		oci.setResourceComment("Enumarion for categories of meals");
		oci.setResourceLabel("MealCategory");
		oci.toEnumeration(new ManagedIndividual[] { MealCategory.BREAKFAST,
				MealCategory.MIDMORNING_SNACK, MealCategory.LUNCH,
				MealCategory.AFTERNOON_SNACK, MealCategory.DINNER,
				MealCategory.AFTERDINNER_SNACK });

		// load Enum. DishCategory
		oci = createNewAbstractOntClassInfo(DishCategory.MY_URI);
		oci.setResourceComment("Enumarion for categories of dishes");
		oci.setResourceLabel("DishCategory");
		oci.toEnumeration(new ManagedIndividual[] { DishCategory.DESSERT,
				DishCategory.DRINK, DishCategory.FIRST_COURSE,
				DishCategory.MAIN_COURSE, DishCategory.SIDE_DISH,
				DishCategory.STARTER, DishCategory.BREAKFAST,
				DishCategory.SNACK });

		// load Enum. MeasureUnit
		oci = createNewAbstractOntClassInfo(MeasureUnit.MY_URI);
		oci.setResourceComment("Enumarion for measure units");
		oci.setResourceLabel("MeasureUnit");
		oci.toEnumeration(new ManagedIndividual[] { MeasureUnit.MILLIGRAMS,
				MeasureUnit.GRAMS, MeasureUnit.KILOGRAMS,
				MeasureUnit.MILLILITERS, MeasureUnit.LITERS, MeasureUnit.CUPS,
				MeasureUnit.TABLESPOONS, MeasureUnit.TEASPOONS,
				MeasureUnit.UNITS, MeasureUnit.HANDFULLS });

		// load Food
		oci = createNewAbstractOntClassInfo(Food.MY_URI);
		oci.setResourceComment("Ingredient");
		oci.setResourceLabel("Ingredient");
		oci.addDatatypeProperty(Food.PROP_ID).setFunctional(); // Integer
		oci.addDatatypeProperty(Food.PROP_CARBOHIDRATES).setFunctional(); // Double
		oci.addDatatypeProperty(Food.PROP_FAT).setFunctional(); // Double
		oci.addDatatypeProperty(Food.PROP_KILOCALORIES).setFunctional(); // Double
		oci.addDatatypeProperty(Food.PROP_NAME).setFunctional(); // String
		oci.addObjectProperty(Food.PROP_FOODSUBCATEGORY).setFunctional(); // FoodSubCategory
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Food.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Food.PROP_CARBOHIDRATES,
						TypeMapper.getDatatypeURI(Double.class), 0, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Food.PROP_FAT,
						TypeMapper.getDatatypeURI(Double.class), 0, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Food.PROP_KILOCALORIES,
						TypeMapper.getDatatypeURI(Double.class), 0, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Food.PROP_NAME,
						TypeMapper.getDatatypeURI(String.class), 1, 1));
		oci.addRestriction(MergedRestriction.getAllValuesRestrictionWithCardinality(
				Food.PROP_FOODSUBCATEGORY,
				TypeMapper.getDatatypeURI(FoodSubCategory.MY_URI), 0, 1));
		
		// load Ingredient
		oci = createNewAbstractOntClassInfo(Ingredient.MY_URI);
		oci.setResourceComment("Ingredient");
		oci.setResourceLabel("Ingredient");
		oci.addDatatypeProperty(Ingredient.PROP_ID).setFunctional(); // Integer
		oci.addDatatypeProperty(Ingredient.PROP_QUANTITY).setFunctional(); // String
		oci.addObjectProperty(Ingredient.PROP_FOOD).setFunctional(); // Food
		oci.addObjectProperty(Ingredient.PROP_MEASURE_UNIT).setFunctional(); // MeasureUnit
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Ingredient.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Ingredient.PROP_QUANTITY,
						TypeMapper.getDatatypeURI(String.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Ingredient.PROP_FOOD,
						TypeMapper.getDatatypeURI(Food.MY_URI), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Ingredient.PROP_MEASURE_UNIT,
						TypeMapper.getDatatypeURI(MeasureUnit.MY_URI), 1, 1));


		// load ShoppingList
		oci = createNewAbstractOntClassInfo(ShoppingList.MY_URI);
		oci.setResourceComment("Shopping list");
		oci.setResourceLabel("ShoppingList");
		oci.addDatatypeProperty(ShoppingList.PROP_ID).setFunctional(); // Integer
		oci.addDatatypeProperty(ShoppingList.PROP_DATEINTERVAL).setFunctional(); // String
		oci.addObjectProperty(ShoppingList.PROP_INGREDIENTS).setFunctional(); // Ingredient
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(ShoppingList.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						ShoppingList.PROP_DATEINTERVAL,
						TypeMapper.getDatatypeURI(String.class), 1, 1));
		oci.addRestriction(MergedRestriction.getAllValuesRestriction( 
				ShoppingList.PROP_INGREDIENTS, Ingredient.MY_URI));
		
		
		// load Recipe
		oci = createNewAbstractOntClassInfo(Recipe.MY_URI);
		oci.setResourceComment("Single recipe");
		oci.setResourceLabel("Recipe");
		oci.addObjectProperty(Recipe.PROP_DISH_CATEGORY).setFunctional(); // DishCategory
		oci.addDatatypeProperty(Recipe.PROP_ID).setFunctional(); // Integer
		oci.addObjectProperty(Recipe.PROP_INGREDIENTS).setFunctional(); // Ingredient
		oci.addDatatypeProperty(Recipe.PROP_IS_FAVOURITE).setFunctional(); // Boolean
		oci.addDatatypeProperty(Recipe.PROP_NAME).setFunctional(); // String
		oci.addDatatypeProperty(Recipe.PROP_PICTURE).setFunctional(); // String
		oci.addDatatypeProperty(Recipe.PROP_PROCEDURE).setFunctional(); // String
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Recipe.PROP_DISH_CATEGORY, DishCategory.MY_URI, 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Recipe.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		System.out.println("Recipe.PropIngredients: "+Recipe.PROP_INGREDIENTS+ "  Ingredient.MyUri: "+Ingredient.MY_URI);
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(
				Recipe.PROP_INGREDIENTS, Ingredient.MY_URI));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Recipe.PROP_IS_FAVOURITE,
						TypeMapper.getDatatypeURI(Boolean.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Recipe.PROP_NAME,
						TypeMapper.getDatatypeURI(String.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Recipe.PROP_PICTURE,
						TypeMapper.getDatatypeURI(String.class), 0, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Recipe.PROP_PROCEDURE,
						TypeMapper.getDatatypeURI(String.class), 0, 1));

		// load Dish
		oci = createNewAbstractOntClassInfo(Dish.MY_URI);
		oci.setResourceComment("Single dish");
		oci.setResourceLabel("Dish");
		oci.addDatatypeProperty(Dish.PROP_ID).setFunctional(); // Integer
		oci.addDatatypeProperty(Dish.PROP_NAME).setFunctional(); // String
		oci.addDatatypeProperty(Dish.PROP_PICTURE).setFunctional(); // String
		oci.addDatatypeProperty(Dish.PROP_CONTAINS_PROCEDURE).setFunctional(); // Boolean
		oci.addDatatypeProperty(Dish.PROP_CONTAINS_RECIPE).setFunctional(); // Boolean
		oci.addObjectProperty(Dish.PROP_DISH_CATEGORY).setFunctional(); // DishCategory
		oci.addObjectProperty(Dish.PROP_RECIPE).setFunctional(); // Recipe
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Dish.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Dish.PROP_NAME,
						TypeMapper.getDatatypeURI(String.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Dish.PROP_PICTURE,
						TypeMapper.getDatatypeURI(String.class), 0, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Dish.PROP_CONTAINS_PROCEDURE,
						TypeMapper.getDatatypeURI(Boolean.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Dish.PROP_CONTAINS_RECIPE,
						TypeMapper.getDatatypeURI(Boolean.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Dish.PROP_DISH_CATEGORY, DishCategory.MY_URI, 1, 1));
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(
				Dish.PROP_RECIPE, Recipe.MY_URI));

		// load Meal
		oci = createNewAbstractOntClassInfo(Meal.MY_URI);
		oci.setResourceComment("Single meal");
		oci.setResourceLabel("Meal");
		oci.addDatatypeProperty(Meal.PROP_ID).setFunctional(); // Integer
		oci.addObjectProperty(Meal.PROP_MEAL_CATEGORY).setFunctional(); // MealCategory
		oci.addObjectProperty(Meal.PROP_DISHES).setFunctional(); // Dish
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(Meal.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						Meal.PROP_MEAL_CATEGORY, DishCategory.MY_URI, 1, 1));
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(
				Meal.PROP_DISHES, Dish.MY_URI));

		// load MenuDay
		oci = createNewAbstractOntClassInfo(MenuDay.MY_URI);
		oci.setResourceComment("Menu for a single day");
		oci.setResourceLabel("MenuDay");
		oci.addDatatypeProperty(MenuDay.PROP_ID).setFunctional(); // Integer
		oci.addObjectProperty(MenuDay.PROP_DAYOFWEEK).setFunctional(); // DayOfWeek
		oci.addObjectProperty(MenuDay.PROP_MEALS).setFunctional(); // Meal
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(MenuDay.PROP_ID,
						TypeMapper.getDatatypeURI(Integer.class), 1, 1));
		oci.addRestriction(MergedRestriction
				.getAllValuesRestrictionWithCardinality(MenuDay.PROP_DAYOFWEEK,
						TypeMapper.getDatatypeURI(Boolean.class), 1, 1));
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(
				MenuDay.PROP_MEALS, Dish.MY_URI));

		
		// load Lighting
		oci = createNewOntClassInfo(NutritionService.MY_URI, factory, NutritionService.Factory_NutritionService);
		oci
			.setResourceComment("The class of services controling light sources");
		oci.setResourceLabel("Lighting");
		oci.addSuperClass(Service.MY_URI);
		oci.addObjectProperty(NutritionService.PROP_GET_RECIPE);
		oci.addRestriction(MergedRestriction.getAllValuesRestriction(
				NutritionService.PROP_GET_RECIPE, NutritionService.MY_URI));


	}
}