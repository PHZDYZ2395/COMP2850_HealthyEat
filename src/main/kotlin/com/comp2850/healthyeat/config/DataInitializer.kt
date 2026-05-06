/**
 * DataInitializer seeds the database with default data on application startup.
 * Creates a default admin user and sample content for recipes, knowledge, and food database.
 * Depends on UserRepository, PasswordEncoder, RecipeRepository, KnowledgeArticleRepository, and FoodDatabaseRepository.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.config

import com.comp2850.healthyeat.domain.*
import com.comp2850.healthyeat.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val recipeRepository: RecipeRepository,
    private val knowledgeArticleRepository: KnowledgeArticleRepository,
    private val foodDatabaseRepository: FoodDatabaseRepository
) : CommandLineRunner {

    /**
     * Initialize default admin user and sample content on startup.
     * @param args command line arguments (unused)
     */
    override fun run(vararg args: String?) {
        if (!userRepository.existsByEmail("admin@example.com")) {
            val admin = userRepository.save(
                User(
                    email = "admin@example.com",
                    passwordHash = passwordEncoder.encode("admin123"),
                    fullName = "System Admin",
                    role = UserRole.ADMIN,
                    enabled = true
                )
            )

            // Sample recipes (26 items to demonstrate pagination)
            recipeRepository.saveAll(listOf(
                Recipe(
                    title = "Mediterranean Salad Bowl",
                    description = "A fresh and healthy Mediterranean salad with mixed greens, cherry tomatoes, cucumbers, and feta cheese.",
                    ingredients = "Mixed greens\nCherry tomatoes\nCucumber\nRed onion\nFeta cheese\nKalamata olives\nOlive oil\nLemon juice",
                    instructions = "Wash and chop all vegetables.\nCombine greens, tomatoes, cucumber, and onion in a large bowl.\nTop with crumbled feta and olives.\nDrizzle with olive oil and lemon juice.\nToss gently and serve.",
                    category = "Salad",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 2,
                    calories = 280,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Grilled Chicken Breast",
                    description = "Perfectly seasoned grilled chicken breast with herbs and spices. High protein, low fat.",
                    ingredients = "Chicken breast\nOlive oil\nGarlic powder\nPaprika\nSalt\nPepper\nLemon",
                    instructions = "Marinate chicken with olive oil, garlic powder, paprika, salt, and pepper for 30 minutes.\nPreheat grill to medium-high heat.\nGrill chicken for 6-7 minutes per side.\nSqueeze lemon juice before serving.",
                    category = "Dinner",
                    difficulty = "Medium",
                    prepTime = 40,
                    servings = 4,
                    calories = 350,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1532550907401-a500c9a57435?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Overnight Oats",
                    description = "Easy and nutritious overnight oats with fresh berries and honey.",
                    ingredients = "Rolled oats\nMilk\nChia seeds\nHoney\nMixed berries\nVanilla extract",
                    instructions = "Combine oats, milk, chia seeds, honey, and vanilla in a jar.\nStir well and cover.\nRefrigerate overnight.\nTop with fresh berries in the morning.",
                    category = "Breakfast",
                    difficulty = "Easy",
                    prepTime = 10,
                    servings = 1,
                    calories = 320,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1517673400267-025145070e68?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Quinoa Buddha Bowl",
                    description = "A colorful and nourishing buddha bowl with quinoa, roasted vegetables, and tahini dressing.",
                    ingredients = "Quinoa\nSweet potato\nChickpeas\nKale\nAvocado\nTahini\nLemon juice\nSesame seeds",
                    instructions = "Cook quinoa according to package instructions.\nRoast sweet potato and chickpeas at 200C for 25 minutes.\nMassage kale with olive oil.\nAssemble bowls with quinoa, roasted veggies, kale, and sliced avocado.\nDrizzle with tahini dressing and sprinkle sesame seeds.",
                    category = "Lunch",
                    difficulty = "Medium",
                    prepTime = 35,
                    servings = 2,
                    calories = 450,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Banana Smoothie",
                    description = "Creamy banana smoothie with almond milk and peanut butter. Perfect post-workout drink.",
                    ingredients = "Banana\nAlmond milk\nPeanut butter\nHoney\nIce cubes",
                    instructions = "Add all ingredients to a blender.\nBlend until smooth and creamy.\nPour into a glass and enjoy immediately.",
                    category = "Snack",
                    difficulty = "Easy",
                    prepTime = 5,
                    servings = 1,
                    calories = 280,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Avocado Toast with Egg",
                    description = "Classic avocado toast topped with a perfectly fried egg. Simple and satisfying.",
                    ingredients = "Whole wheat bread\nAvocado\nEgg\nSalt\nPepper\nRed pepper flakes\nLemon juice",
                    instructions = "Toast the bread until golden.\nMash avocado with lemon juice, salt, and pepper.\nSpread avocado on toast.\nFry an egg to your liking.\nTop the toast with the egg and sprinkle red pepper flakes.",
                    category = "Breakfast",
                    difficulty = "Easy",
                    prepTime = 10,
                    servings = 1,
                    calories = 350,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Thai Green Curry",
                    description = "Aromatic Thai green curry with coconut milk, vegetables, and fragrant herbs.",
                    ingredients = "Green curry paste\nCoconut milk\nChicken thigh\nThai eggplant\nBasil\nFish sauce\nPalm sugar",
                    instructions = "Fry curry paste in coconut cream.\nAdd chicken and cook until done.\nPour in coconut milk and vegetables.\nSimmer until vegetables are tender.\nSeason with fish sauce and sugar.\nGarnish with basil leaves.",
                    category = "Dinner",
                    difficulty = "Medium",
                    prepTime = 30,
                    servings = 3,
                    calories = 420,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Greek Yogurt Parfait",
                    description = "Layered Greek yogurt with granola, fresh fruits, and honey drizzle.",
                    ingredients = "Greek yogurt\nGranola\nStrawberries\nBlueberries\nHoney\nChia seeds",
                    instructions = "Layer yogurt in a glass.\nAdd granola on top.\nAdd sliced strawberries and blueberries.\nRepeat layers.\nDrizzle with honey and sprinkle chia seeds.",
                    category = "Breakfast",
                    difficulty = "Easy",
                    prepTime = 5,
                    servings = 1,
                    calories = 290,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1488477181980-32a04084389e?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Veggie Stir Fry",
                    description = "Quick and colorful vegetable stir fry with soy ginger sauce.",
                    ingredients = "Bell pepper\nBroccoli\nCarrot\nSnap peas\nSoy sauce\nGinger\nGarlic\nSesame oil",
                    instructions = "Heat sesame oil in a wok.\nStir fry garlic and ginger.\nAdd vegetables and cook on high heat.\nAdd soy sauce and toss.\nServe over steamed rice.",
                    category = "Lunch",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 2,
                    calories = 220,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1512058564366-18510be2db12?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Berry Chia Pudding",
                    description = "Creamy chia seed pudding topped with mixed berries. High in fiber and omega-3.",
                    ingredients = "Chia seeds\nCoconut milk\nVanilla extract\nMaple syrup\nMixed berries",
                    instructions = "Mix chia seeds with coconut milk.\nAdd vanilla and maple syrup.\nRefrigerate for at least 4 hours.\nTop with fresh berries before serving.",
                    category = "Dessert",
                    difficulty = "Easy",
                    prepTime = 10,
                    servings = 2,
                    calories = 210,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1493723843671-1d655e66ac1c?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Lentil Soup",
                    description = "Hearty and nutritious lentil soup with tomatoes and spices.",
                    ingredients = "Red lentils\nOnion\nGarlic\nTomatoes\nCumin\nTurmeric\nVegetable broth\nLemon juice",
                    instructions = "Saute onion and garlic.\nAdd cumin and turmeric.\nAdd lentils, tomatoes, and broth.\nSimmer for 25 minutes.\nBlend until smooth.\nFinish with lemon juice.",
                    category = "Lunch",
                    difficulty = "Easy",
                    prepTime = 35,
                    servings = 4,
                    calories = 260,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Protein Energy Balls",
                    description = "No-bake energy balls with oats, peanut butter, and chocolate chips.",
                    ingredients = "Rolled oats\nPeanut butter\nHoney\nDark chocolate chips\nVanilla extract\nFlax seeds",
                    instructions = "Mix all ingredients in a bowl.\nRefrigerate for 30 minutes.\nRoll into small balls.\nStore in the refrigerator.",
                    category = "Snack",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 12,
                    calories = 120,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Salmon Poke Bowl",
                    description = "Fresh salmon poke bowl with avocado, edamame, and sushi rice.",
                    ingredients = "Salmon\nSushi rice\nAvocado\nEdamame\nSoy sauce\nSesame seeds\nNori\nRice vinegar",
                    instructions = "Cook sushi rice and season with rice vinegar.\nCube fresh salmon and marinate in soy sauce.\nArrange rice, salmon, avocado, and edamame in a bowl.\nTop with sesame seeds and nori.",
                    category = "Lunch",
                    difficulty = "Medium",
                    prepTime = 25,
                    servings = 2,
                    calories = 480,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Turkey Meatballs",
                    description = "Lean turkey meatballs baked to perfection with Italian herbs.",
                    ingredients = "Ground turkey\nBreadcrumbs\nEgg\nParmesan\nItalian seasoning\nGarlic\nOnion",
                    instructions = "Mix all ingredients in a bowl.\nForm into meatballs.\nBake at 200C for 20 minutes.\nServe with marinara sauce or over pasta.",
                    category = "Dinner",
                    difficulty = "Medium",
                    prepTime = 35,
                    servings = 4,
                    calories = 310,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Mango Lassi",
                    description = "Creamy Indian-style mango yogurt drink. Refreshing and nutritious.",
                    ingredients = "Mango\nYogurt\nMilk\nHoney\nCardamom\nIce cubes",
                    instructions = "Add all ingredients to a blender.\nBlend until smooth and creamy.\nPour into glasses and serve chilled.",
                    category = "Snack",
                    difficulty = "Easy",
                    prepTime = 5,
                    servings = 2,
                    calories = 180,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1546173159-315724a31b96?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Egg Fried Rice",
                    description = "Quick and delicious egg fried rice with vegetables and soy sauce.",
                    ingredients = "Cooked rice\nEggs\nPeas\nCarrots\nSoy sauce\nSesame oil\nGreen onions\nGarlic",
                    instructions = "Scramble eggs in a hot wok.\nAdd vegetables and garlic.\nAdd cold cooked rice.\nStir fry with soy sauce and sesame oil.\nGarnish with green onions.",
                    category = "Dinner",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 2,
                    calories = 380,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Caprese Sandwich",
                    description = "Fresh mozzarella, tomato, and basil sandwich with balsamic glaze.",
                    ingredients = "Ciabatta bread\nFresh mozzarella\nTomato\nFresh basil\nBalsamic glaze\nOlive oil\nSalt",
                    instructions = "Slice ciabatta and toast lightly.\nLayer mozzarella, tomato slices, and basil.\nDrizzle with olive oil and balsamic glaze.\nSeason with salt and serve.",
                    category = "Lunch",
                    difficulty = "Easy",
                    prepTime = 10,
                    servings = 2,
                    calories = 340,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1550507992-eb63ffee0817?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Sweet Potato Pancakes",
                    description = "Fluffy sweet potato pancakes with maple syrup. A healthy breakfast twist.",
                    ingredients = "Sweet potato\nFlour\nEggs\nMilk\nBaking powder\nCinnamon\nMaple syrup",
                    instructions = "Mash cooked sweet potato.\nMix with flour, eggs, milk, baking powder, and cinnamon.\nCook pancakes on a griddle until golden.\nServe with maple syrup.",
                    category = "Breakfast",
                    difficulty = "Medium",
                    prepTime = 25,
                    servings = 3,
                    calories = 290,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Chicken Caesar Wrap",
                    description = "Crispy chicken caesar salad wrapped in a tortilla for a portable lunch.",
                    ingredients = "Tortilla\nChicken breast\nRomaine lettuce\nParmesan\nCaesar dressing\nCroutons",
                    instructions = "Grill and slice chicken breast.\nToss lettuce with caesar dressing.\nLayer chicken, lettuce, parmesan, and croutons on tortilla.\nRoll tightly and slice in half.",
                    category = "Lunch",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 2,
                    calories = 410,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Dark Chocolate Mousse",
                    description = "Rich and creamy dark chocolate mousse. A decadent yet portion-controlled dessert.",
                    ingredients = "Dark chocolate\nHeavy cream\nEgg yolks\nSugar\nVanilla extract",
                    instructions = "Melt dark chocolate.\nWhip cream to stiff peaks.\nFold egg yolks and sugar into chocolate.\nFold in whipped cream.\nChill for 2 hours before serving.",
                    category = "Dessert",
                    difficulty = "Hard",
                    prepTime = 30,
                    servings = 4,
                    calories = 320,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1511381939415-e44015466834?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Tuna Niçoise Salad",
                    description = "Classic French salad with tuna, green beans, potatoes, and olives.",
                    ingredients = "Tuna\nGreen beans\nPotatoes\nEggs\nCherry tomatoes\nOlives\nOlive oil\nDijon mustard",
                    instructions = "Boil potatoes and eggs.\nBlanch green beans.\nArrange all ingredients on a plate.\nWhisk olive oil and Dijon for dressing.\nDrizzle dressing over salad.",
                    category = "Salad",
                    difficulty = "Medium",
                    prepTime = 30,
                    servings = 2,
                    calories = 390,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1505253758473-96b701592919?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Vegan Black Bean Tacos",
                    description = "Spicy black bean tacos with avocado salsa and pickled onions.",
                    ingredients = "Tortillas\nBlack beans\nAvocado\nRed onion\nLime\nCumin\nChili powder\nCilantro",
                    instructions = "Season and heat black beans with cumin and chili powder.\nMake avocado salsa with lime and cilantro.\nPickle red onions.\nWarm tortillas and assemble tacos.",
                    category = "Dinner",
                    difficulty = "Easy",
                    prepTime = 20,
                    servings = 3,
                    calories = 310,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1551504734-801e87d30314?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Hummus and Veggie Platter",
                    description = "Creamy homemade hummus served with fresh vegetable sticks.",
                    ingredients = "Chickpeas\nTahini\nLemon juice\nGarlic\nOlive oil\nCarrots\nCelery\nCucumber",
                    instructions = "Blend chickpeas, tahini, lemon juice, and garlic.\nAdd olive oil while blending.\nSeason with salt and cumin.\nServe with fresh vegetable sticks.",
                    category = "Snack",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 4,
                    calories = 150,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1577805947697-89e18249d767?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Shrimp Pad Thai",
                    description = "Classic Thai stir-fried noodles with shrimp, peanuts, and tamarind sauce.",
                    ingredients = "Rice noodles\nShrimp\nEggs\nBean sprouts\nPeanuts\nTamarind paste\nFish sauce\nLime",
                    instructions = "Soak rice noodles.\nStir fry shrimp and push aside.\nScramble eggs in the wok.\nAdd noodles and sauce.\nToss with bean sprouts.\nTop with peanuts and lime.",
                    category = "Dinner",
                    difficulty = "Medium",
                    prepTime = 30,
                    servings = 2,
                    calories = 460,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1559314809-0d155014e29e?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Apple Cinnamon Oatmeal",
                    description = "Warm and comforting oatmeal with diced apples, cinnamon, and walnuts.",
                    ingredients = "Rolled oats\nApple\nCinnamon\nWalnuts\nMilk\nMaple syrup\nButter",
                    instructions = "Cook oats in milk.\nDice apple and saute with butter and cinnamon.\nTop oatmeal with apple mixture.\nAdd walnuts and drizzle maple syrup.",
                    category = "Breakfast",
                    difficulty = "Easy",
                    prepTime = 15,
                    servings = 1,
                    calories = 340,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1589367920969-ab8e050bbb04?w=400&h=300&fit=crop"
                ),
                Recipe(
                    title = "Stuffed Bell Peppers",
                    description = "Colorful bell peppers stuffed with seasoned ground beef, rice, and cheese.",
                    ingredients = "Bell peppers\nGround beef\nRice\nTomato sauce\nOnion\nGarlic\nCheddar cheese\nItalian seasoning",
                    instructions = "Cut tops off peppers and remove seeds.\nBrown beef with onion and garlic.\nMix with cooked rice and tomato sauce.\nStuff peppers and top with cheese.\nBake at 190C for 25 minutes.",
                    category = "Dinner",
                    difficulty = "Medium",
                    prepTime = 45,
                    servings = 4,
                    calories = 380,
                    createdBy = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1511688878353-3a2f5be94cd7?w=400&h=300&fit=crop"
                )
            ))

            // Sample knowledge articles (16 items to demonstrate pagination)
            knowledgeArticleRepository.saveAll(listOf(
                KnowledgeArticle(
                    title = "Understanding Macronutrients: A Complete Guide",
                    content = "Macronutrients are the nutrients your body needs in large amounts: carbohydrates, proteins, and fats. Each plays a vital role in maintaining your health.\n\n**Carbohydrates** are your body's primary energy source. They include sugars, starches, and fiber found in fruits, grains, vegetables, and dairy products.\n\n**Proteins** are essential for building and repairing tissues, making enzymes and hormones, and supporting immune function. Good sources include meat, fish, eggs, beans, and nuts.\n\n**Fats** are necessary for energy storage, cell growth, and nutrient absorption. Focus on healthy fats from olive oil, avocados, nuts, and fatty fish.",
                    summary = "Learn about the three macronutrients - carbohydrates, proteins, and fats - and their essential roles in your diet.",
                    category = "Nutrition",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "10 Tips for Healthy Eating Habits",
                    content = "1. Eat a variety of foods to ensure balanced nutrition.\n2. Include plenty of fruits and vegetables in your daily diet.\n3. Choose whole grains over refined grains.\n4. Limit added sugars and sugary drinks.\n5. Reduce sodium intake by cooking at home.\n6. Stay hydrated - drink at least 8 glasses of water daily.\n7. Eat mindfully and avoid distractions during meals.\n8. Plan your meals ahead to avoid unhealthy choices.\n9. Don't skip breakfast - it kickstarts your metabolism.\n10. Practice portion control to maintain a healthy weight.",
                    summary = "Practical tips to develop and maintain healthy eating habits for a better lifestyle.",
                    category = "Diet Tips",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "The Benefits of Regular Exercise",
                    content = "Regular physical activity is one of the most important things you can do for your health. Benefits include:\n\n- Weight management\n- Reduced risk of heart disease and type 2 diabetes\n- Stronger bones and muscles\n- Improved mental health and mood\n- Better sleep quality\n- Increased energy levels\n\nAim for at least 150 minutes of moderate aerobic activity or 75 minutes of vigorous activity per week, combined with muscle-strengthening exercises on 2 or more days.",
                    summary = "Discover the numerous health benefits of regular physical exercise and recommended activity levels.",
                    category = "Exercise",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1571019614242-c5c5dee9f50b?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "How to Calculate Your Daily Calorie Needs",
                    content = "Your daily calorie needs depend on your age, gender, weight, height, and activity level. Here's a simple approach:\n\n1. Calculate your Basal Metabolic Rate (BMR) using the Mifflin-St Jeor equation.\n2. Multiply by your activity factor:\n   - Sedentary: 1.2\n   - Lightly active: 1.375\n   - Moderately active: 1.55\n   - Very active: 1.725\n3. Adjust based on your goal: subtract 500 for weight loss, add 500 for weight gain.",
                    summary = "Learn how to calculate your daily calorie requirements based on your personal factors and goals.",
                    category = "Weight Loss",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Mindful Eating for Better Mental Health",
                    content = "Mindful eating is about being fully present during meals. It involves:\n\n- Eating slowly and without distraction\n- Listening to your body's hunger and fullness cues\n- Engaging all your senses while eating\n- Acknowledging your food preferences without judgment\n\nResearch shows that mindful eating can reduce stress, improve digestion, and foster a healthier relationship with food.",
                    summary = "Explore the connection between mindful eating practices and improved mental well-being.",
                    category = "Mental Health",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1505576399279-0d754c0ce141?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "The Power of Hydration: Why Water Matters",
                    content = "Water is essential for every cell in your body. Proper hydration helps:\n\n- Regulate body temperature\n- Transport nutrients and oxygen\n- Flush out waste products\n- Lubricate joints\n- Improve skin health\n\nAim for at least 8 glasses (2 liters) per day. More if you exercise or live in a hot climate. Add lemon or cucumber for flavor if plain water is boring.",
                    summary = "Understanding the critical role of water in maintaining optimal health and daily energy levels.",
                    category = "Nutrition",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1548839140-2f1472cc16ef?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Understanding Food Labels: What to Look For",
                    content = "Reading food labels is a crucial skill for making informed dietary choices. Here's what to focus on:\n\n1. Serving Size - All nutritional info is based on this.\n2. Calories - Check both per serving and per package.\n3. Saturated Fat - Should be limited to less than 10% of daily calories.\n4. Added Sugars - The less, the better.\n5. Sodium - Aim for less than 2,300mg per day.\n6. Ingredients - Listed by quantity; avoid products with long lists of unrecognizable ingredients.",
                    summary = "A practical guide to reading and understanding nutrition labels on packaged foods.",
                    category = "Nutrition",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Meal Prep for Beginners: A Step-by-Step Guide",
                    content = "Meal prepping saves time, money, and helps you eat healthier. Follow these steps:\n\n1. Plan your meals for the week.\n2. Make a shopping list and stick to it.\n3. Cook in batches - grains, proteins, and vegetables.\n4. Store in portion-sized containers.\n5. Label everything with dates.\n6. Most prepped meals last 3-4 days in the fridge.\n\nStart with just 2-3 meals per week and build up as you get comfortable.",
                    summary = "Learn the basics of meal preparation to save time and eat healthier throughout the week.",
                    category = "Diet Tips",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1556909114-44e3e70034e2?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "HIIT vs Steady-State Cardio: Which Is Better?",
                    content = "Both High-Intensity Interval Training (HIIT) and steady-state cardio have their place:\n\n**HIIT:**\n- Burns more calories in less time\n- Boosts metabolism for hours after workout\n- Can be done with any exercise\n- Best for 2-3 sessions per week\n\n**Steady-State Cardio:**\n- Easier on the joints\n- Great for building endurance\n- Can be done daily\n- Ideal for recovery days\n\nThe best approach combines both for optimal results.",
                    summary = "Compare high-intensity interval training with steady-state cardio to find what works for you.",
                    category = "Exercise",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1434608519344-49d77a699e1d?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "The Gut-Brain Connection: How Diet Affects Mood",
                    content = "Your gut and brain are intimately connected through the vagus nerve. The gut microbiome influences:\n\n- Serotonin production (90% is made in the gut)\n- Stress response\n- Cognitive function\n- Emotional regulation\n\nFoods that support gut health:\n- Probiotic-rich foods (yogurt, kefir, kimchi)\n- Prebiotic fiber (garlic, onions, bananas)\n- Omega-3 fatty acids (salmon, walnuts)\n- Fermented foods (sauerkraut, kombucha)",
                    summary = "Explore how the foods you eat influence your mental health through the gut microbiome.",
                    category = "Mental Health",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1505576399279-0d754c0ce141?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Intermittent Fasting: Science and Practice",
                    content = "Intermittent fasting (IF) alternates between eating and fasting periods. Popular methods:\n\n1. 16/8 - Fast 16 hours, eat within an 8-hour window\n2. 5:2 - Eat normally 5 days, restrict to 500-600 calories 2 days\n3. Eat-Stop-Eat - 24-hour fast once or twice per week\n\nBenefits supported by research:\n- Improved insulin sensitivity\n- Weight loss\n- Reduced inflammation\n- Enhanced brain function\n\nConsult a healthcare provider before starting, especially if you have medical conditions.",
                    summary = "Understanding the science behind intermittent fasting and how to practice it safely.",
                    category = "Weight Loss",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1506126613408-16356165878c?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Strength Training for Beginners",
                    content = "Strength training builds muscle, strengthens bones, and boosts metabolism. Getting started:\n\n1. Start with bodyweight exercises (squats, push-ups, planks)\n2. Focus on form over weight\n3. Train all major muscle groups\n4. Rest 48 hours between sessions for the same muscle group\n5. Progress gradually - increase weight by no more than 10% per week\n\nAim for 2-3 sessions per week, 30-45 minutes each. Compound exercises like squats, deadlifts, and bench presses give the most benefit.",
                    summary = "A beginner's guide to starting a strength training routine safely and effectively.",
                    category = "Exercise",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1534368786749-b63e05c90863?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Healthy Snacking: What to Eat Between Meals",
                    content = "Smart snacking can keep energy levels stable and prevent overeating at meals. Good options:\n\n- Apple slices with peanut butter\n- Greek yogurt with berries\n- Handful of mixed nuts\n- Veggie sticks with hummus\n- Hard-boiled eggs\n- Cottage cheese with fruit\n\nTips:\n- Keep portions small (150-200 calories)\n- Combine protein with fiber for sustained energy\n- Avoid mindless snacking from a bag",
                    summary = "Discover nutritious snack options that satisfy hunger without derailing your health goals.",
                    category = "Diet Tips",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Sleep and Nutrition: The Overlooked Connection",
                    content = "Quality sleep is essential for health, and nutrition plays a surprising role:\n\n**Foods that help sleep:**\n- Tart cherry juice (natural melatonin)\n- Kiwi (serotonin precursor)\n- Warm milk (tryptophan)\n- Almonds (magnesium)\n\n**Foods that disrupt sleep:**\n- Caffeine (avoid after 2 PM)\n- Heavy meals within 3 hours of bedtime\n- Alcohol (reduces REM sleep)\n- Spicy foods (can cause heartburn)\n\nAim for 7-9 hours of quality sleep per night for optimal health.",
                    summary = "How the foods and drinks you consume affect your sleep quality and overall health.",
                    category = "Mental Health",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1531353826077-e07099ced0fc?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Plant-Based Protein: Complete Guide",
                    content = "You don't need meat to get enough protein. Excellent plant-based sources:\n\n- Lentils (18g per cup cooked)\n- Chickpeas (15g per cup)\n- Tofu (20g per cup)\n- Quinoa (8g per cup)\n- Black beans (15g per cup)\n- Hemp seeds (10g per 3 tbsp)\n- Spirulina (4g per tbsp)\n\nTo get complete proteins, combine different plant sources throughout the day. For example: rice and beans, or hummus and pita.",
                    summary = "A comprehensive guide to getting sufficient protein from plant-based food sources.",
                    category = "Nutrition",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&h=300&fit=crop"
                ),
                KnowledgeArticle(
                    title = "Setting Realistic Fitness Goals",
                    content = "Effective fitness goals follow the SMART framework:\n\n**Specific** - 'Lose weight' is vague. 'Lose 5kg in 3 months' is specific.\n**Measurable** - Track progress with numbers.\n**Achievable** - Challenge yourself but be realistic.\n**Relevant** - Align with your overall health vision.\n**Time-bound** - Set a deadline.\n\nExamples:\n- Run 5K without stopping in 8 weeks\n- Do 10 push-ups in 4 weeks\n- Exercise 3 times per week for the next month\n\nReview and adjust your goals monthly.",
                    summary = "Learn how to set effective, achievable fitness goals using the SMART framework.",
                    category = "Exercise",
                    authorId = admin.id!!,
                    imageUrl = "https://images.unsplash.com/photo-1483721310020-03333e577078?w=400&h=300&fit=crop"
                )
            ))

            // Sample food database (28 items to demonstrate pagination)
            foodDatabaseRepository.saveAll(listOf(
                FoodDatabase(name = "Apple", category = "Fruit", caloriesPer100g = 52, protein = 0.3, carbs = 14.0, fat = 0.2, fiber = 2.4),
                FoodDatabase(name = "Banana", category = "Fruit", caloriesPer100g = 89, protein = 1.1, carbs = 23.0, fat = 0.3, fiber = 2.6),
                FoodDatabase(name = "Chicken Breast", category = "Meat", caloriesPer100g = 165, protein = 31.0, carbs = 0.0, fat = 3.6, fiber = 0.0),
                FoodDatabase(name = "Brown Rice", category = "Grain", caloriesPer100g = 112, protein = 2.6, carbs = 24.0, fat = 0.9, fiber = 1.8),
                FoodDatabase(name = "Broccoli", category = "Vegetable", caloriesPer100g = 34, protein = 2.8, carbs = 7.0, fat = 0.4, fiber = 2.6),
                FoodDatabase(name = "Salmon", category = "Seafood", caloriesPer100g = 208, protein = 20.0, carbs = 0.0, fat = 13.0, fiber = 0.0),
                FoodDatabase(name = "Greek Yogurt", category = "Dairy", caloriesPer100g = 59, protein = 10.0, carbs = 3.6, fat = 0.7, fiber = 0.0),
                FoodDatabase(name = "Almonds", category = "Snack", caloriesPer100g = 579, protein = 21.0, carbs = 22.0, fat = 50.0, fiber = 12.5),
                FoodDatabase(name = "Sweet Potato", category = "Vegetable", caloriesPer100g = 86, protein = 1.6, carbs = 20.0, fat = 0.1, fiber = 3.0),
                FoodDatabase(name = "Avocado", category = "Fruit", caloriesPer100g = 160, protein = 2.0, carbs = 8.5, fat = 15.0, fiber = 6.7),
                FoodDatabase(name = "Egg", category = "Dairy", caloriesPer100g = 155, protein = 13.0, carbs = 1.1, fat = 11.0, fiber = 0.0),
                FoodDatabase(name = "Oats", category = "Grain", caloriesPer100g = 389, protein = 17.0, carbs = 66.0, fat = 7.0, fiber = 11.0),
                FoodDatabase(name = "Orange", category = "Fruit", caloriesPer100g = 47, protein = 0.9, carbs = 12.0, fat = 0.1, fiber = 2.4),
                FoodDatabase(name = "Strawberry", category = "Fruit", caloriesPer100g = 33, protein = 0.7, carbs = 7.7, fat = 0.3, fiber = 2.0),
                FoodDatabase(name = "Tuna", category = "Seafood", caloriesPer100g = 132, protein = 29.0, carbs = 0.0, fat = 1.3, fiber = 0.0),
                FoodDatabase(name = "Beef Steak", category = "Meat", caloriesPer100g = 271, protein = 26.0, carbs = 0.0, fat = 18.0, fiber = 0.0),
                FoodDatabase(name = "Spinach", category = "Vegetable", caloriesPer100g = 23, protein = 2.9, carbs = 3.6, fat = 0.4, fiber = 2.2),
                FoodDatabase(name = "Whole Milk", category = "Dairy", caloriesPer100g = 61, protein = 3.2, carbs = 4.8, fat = 3.3, fiber = 0.0),
                FoodDatabase(name = "Cheddar Cheese", category = "Dairy", caloriesPer100g = 403, protein = 25.0, carbs = 1.3, fat = 33.0, fiber = 0.0),
                FoodDatabase(name = "White Bread", category = "Grain", caloriesPer100g = 265, protein = 9.0, carbs = 49.0, fat = 3.2, fiber = 2.7),
                FoodDatabase(name = "Potato", category = "Vegetable", caloriesPer100g = 77, protein = 2.0, carbs = 17.0, fat = 0.1, fiber = 2.2),
                FoodDatabase(name = "Pork Chop", category = "Meat", caloriesPer100g = 231, protein = 27.0, carbs = 0.0, fat = 13.0, fiber = 0.0),
                FoodDatabase(name = "Shrimp", category = "Seafood", caloriesPer100g = 99, protein = 24.0, carbs = 0.2, fat = 0.3, fiber = 0.0),
                FoodDatabase(name = "Walnuts", category = "Snack", caloriesPer100g = 654, protein = 15.0, carbs = 14.0, fat = 65.0, fiber = 6.7),
                FoodDatabase(name = "Quinoa", category = "Grain", caloriesPer100g = 120, protein = 4.4, carbs = 21.0, fat = 1.9, fiber = 2.8),
                FoodDatabase(name = "Carrot", category = "Vegetable", caloriesPer100g = 41, protein = 0.9, carbs = 10.0, fat = 0.2, fiber = 2.8),
                FoodDatabase(name = "Blueberry", category = "Fruit", caloriesPer100g = 57, protein = 0.7, carbs = 14.0, fat = 0.3, fiber = 2.4),
                FoodDatabase(name = "Tofu", category = "Meat", caloriesPer100g = 76, protein = 8.0, carbs = 1.9, fat = 4.8, fiber = 0.3)
            ))
        }
    }
}
