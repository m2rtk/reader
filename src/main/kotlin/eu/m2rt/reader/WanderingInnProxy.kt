package eu.m2rt.reader

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.plugin.Plugin
import io.javalin.http.Context
import org.jsoup.Jsoup
import java.net.URL
import java.util.*

class WanderingInnProxy(
    private val wrapperHtml: String = getResourceAsText("/wandering-inn-wrapper.html")
): Plugin {

    override fun apply(app: Javalin) {
        app.routes {
            ApiBuilder.get("/wandering-inn") {
                it.renderWanderingInn("https://wanderinginn.com/table-of-contents/")
            }

            ApiBuilder.get("/wandering-inn/*") {
                it.renderWanderingInn("https://wanderinginn.com/${it.path().removePrefix("/wandering-inn/")}")
            }
        }
    }

    private fun Context.renderWanderingInn(originalUrl: String) {
        val document = Jsoup.parse(
            URL(originalUrl),
            10_000
        )

        val article = document.body()
            .getFirst("#content article")

        article.select("a[href]").forEach { a ->
            a.attr(
                "href",
                a.attr("href")
                    .replaceFirst("https://wanderinginn.wordpress.com/", "/wandering-inn/")
                    .replaceFirst("https://wanderinginn.com/", "/wandering-inn/")
            )
        }

        resultHtml(
            wrapperHtml
                .replace("\${INFO}", article.getFirst(".entry-content").text().countWords().toString())
                .replace("\${ORIGINAL_URL}", originalUrl)
                .replace("\${CONTENT}", article.outerHtml())
        )
    }

    private fun String.countWords(): Int = StringTokenizer(this).countTokens()
}

fun main() {
    val asd = """
        Erin woke up. Generally this was an ordeal. Today however, it was fairly easy. Because the real ordeal would come later.

        Such as right after breakfast. Erin stared glumly at the three shriveled blue fruits on her plate. She bit the first experimentally and chewed. And chewed. And chewed.

        “Rubbery.”

        It was incredibly difficult to chew the fruits. The skin on these ones were so tough to bite into, it did remind Erin of eating rubber. Not that she’d ever done that since she was a baby.

        Plus, they’d lost their delicious juices and tasted—well, flat. There was no sweetness left in them, and they were quite, quite unappetizing when you put all these qualities together. But Erin ate them, mainly because she had nothing left to eat.

        “I’m in trouble. Yup, yup.”

        It wasn’t that she was out of blue fruits. There were plenty—well, some—still ready to be harvested from the orchard. But they, like all food, were in limited supply. Besides, the issue wasn’t that. It was her guests.

        “Who’d want to eat blue fruits all day? Raise your hand if that sounds like fun.”

        Erin didn’t raise her hand. Granted, they were tasty and made a good fruit drink, but when you got down to it, they were still just fruits.

        “And I want food. Real food. Not fruit. I want bread! I want pasta! I want pizza and soda and salad and ice cream—actually forget the ice cream. I need meat. Or fish that doesn’t bite back! I want sushi, cheeseburgers and fries, toast, waffles…cereal…”

        Erin pressed her hands to her rumbling stomach and tried not to cry.

        “Even instant ramen would be nice. Is that too much to ask?”

        It was. She knew that. But just thinking about the food made her tear up a bit. She could handle Goblins. She could deal with rude Necromancers and fight off evil rock-crabs. She could even handle giant fish that tried to nibble on her when she took a bath. But she wanted food.

        “Plus, I need to feed my guests.”

        Erin nodded. The math was simple. No food = no guests = no money = starvation. But the little flaw in the equation was that in order to get the food, she’d need to spend the money. And she had no way of doing that.

        “Unless I go to the city.”

        Now, that was a thought. She wasn’t sure if that was a good thought, but it was the only option she had available. The city. Erin went to the window. Relc had shown her where it was…

        “There.”

        Erin stared at the small buildings in the distance. It looked far. But then, everything looked far around here. And the city would have things. Like food. And clothing. And toothbrushes. Still, Erin didn’t want to go.

        “It’s far. But I have to go. Maybe? Yes…no. No? Yes. I need food. And I need to feed my guests. It’s my duty as an innkeeper.”

        She paused and thought about that last statement. Erin collapsed into a chair and cradled her head in her hands.

        “Am I an innkeeper? Is that what this world is doing to me?”

        Maybe. It was probably the [Innkeeper] class.

        “Soon I’ll grow a huge beer belly and start hauling around kegs of ale. That’s what innkeepers do, right?”

        She didn’t actually know. It wasn’t as if she’d ever paid that much attention to medieval history, at least the parts that were actually history.

        “They never mentioned innkeepers in the legend of King Arthur. Or did they?”

        There was no Google to help her so Erin abandoned that train of thought. Really, she was distracting herself. The problem she was facing was simple.

        “To go to the city or not, that is the question. Actually, there’s no question. I need to go to the city. I need to go…shopping.”

        Shopping. It would be a lot more appealing if she wasn’t trying to buy things to survive. But it had to be done. She knew it.

        Still. Erin really, really didn’t like that idea. She liked people, she really did. But she had a negative reaction to A: leaving her safe inn, and B: travelling to a far off city probably full of giant lizards and insects that walked on two feet.

        Glumly, she stared at the three sticky blue fruit cores on her plate. She walked outside and threw them as far as she could. The juices left her hands feeling unpleasantly sticky, but there wasn’t much she could do about it.

        “Guess I’ve gotta go to the stream. Who knew washing your hands was so much work?”

        Erin grumbled as she wiped her hand on her jeans. Then she paused. And looked down.

        Her jeans were blue. The blue fruit juice was blue. But against all odds, the blue stain still showed up quite visibly on her clothing. Or rather, the blue fruit stains. And they weren’t just on her pants.

        Erin’s shirt was a nice, commercial t-shirt with a lovely company logo on the front and back. Really, she wasn’t that attached to it, but it was perfect to wear when she was just staying at home. It wasn’t her choice of clothing.

        …Which was good, because Erin would have cried if she’d inflicted the same damage on a t-shirt she really liked. She gazed down at the blue stains covering her shirt. She poked at the rips and cuts on the sleeves and the burn marks on one side. She lifted the shirt, sniffed once, and gagged.

        For the first time Erin felt at her hair. She raised a hand and smelled her breath. She thought about the last time she’d brushed her teeth, taken a bath, or even used soap. Then she tried to shut down her mind.

        “Well, that settles that. I’m off to the city.”

         

        —-

         

        Erin walked through the grass. She wished there was a nice road to follow, but for some reason no one bothered to pave a road through the empty wilderness. Come to that, she wondered again why anyone would build an inn in the middle of nowhere.

        Maybe there used to be more people in the area. Or maybe there was just an idiot who thought he was breaking into an untapped market. Either way, Erin was grateful for the inn.

        “But why does it have to be so far away from anything?”

        Erin walked down the slope. At least there was that. The inn was located on an incline. Not a steep hill, but a really long slope that gradually went down the more she walked. It was nice, until Erin looked back and realized she’d be climbing up all that way again soon.

        “Wow. That’s a big hill.”

        She stared for a while and kept walking. Relc and Klbkch had called the journey to the city a walk of about twenty minutes.

        “They lied to me.”

        Or maybe they just walked really fast. Erin could actually see the city Klbkch had called Liscor in the distance. It was still small, but given how close it seemed now compared to before and multiplying her velocity by her legs and given energy divided by her willingness to keep walking…

        “Thirty minutes. No; probably an hour. Yeah, that sounds about right.”

        Erin sighed. But exercise was good for her, right? It built character. Or something.

        “So, what do I need?”

        She took a quick inventory check. Her coins were securely packed into the bottom of one pocket. They were heavy. She had her clothes on, which was important, and she looked like…well, like a homeless person. But she had money. So what should she buy with it?

        “Um. Clothing. Right. And soap. And a toothbrush, if they have toothbrushes. And toothpaste…which they probably don’t have. But something. And I need food obviously, soap, towels, laundry deter—more soap, and a comb.”

        Erin walked a few more feet.

        “And a sword. I need a sword. And a shield? And armor? And uh, anti-Goblin spray? Oh, and books! Tons of books. Maps, history books…can I read any of that? Well, Relc and Klbkch speak English. So that’s weird too. And I need bandages, a sewing needle, someone to teach me how to sew…”

        Erin felt at her pocket. The coins jingled. She wished there were more to jingle.

        “And I need to rob a bank.”

        Okay. Erin retraced her thoughts.

        “What’s essential?”

        She counted off on her fingers.

        “Clothing. Food. Toothbrush. Soap. And a lamp.”

        She snapped her fingers.

        “Right. A lamp! And a sword.”

        She felt at her pocket.

        “…Just the lamp.”

         

        —-

         

        “Flat grass, flat grass, all I see is flat grass.”

        Erin sang as she walked. She wasn’t sure if there was a tune, but at least the singing kept her company.

        “Horses eat grass, but I’ll pass, so I’ll go to the city fast. Or I’ll die of starvation! And once I’m there I’ll eat ten pears and—hey, is that a Goblin?”

        Erin turned her head suddenly and the small head ducked down. She squinted. Yes, that was definitely a Goblin. It was hiding up on a small hill to her left, but she knew it was still there. Watching her.

        Well. She was being followed. Erin wasn’t sure what to make of that. She looked around and two more heads disappeared as their owners dove for cover. They didn’t look like they were trying to ambush her, just follow her.

        “Hm.”

        Erin bent down and searched the grass. Eventually she found what she was looking for. She waited until one of the Goblins decided she’d forgotten about them and poked his head up again. Then she turned and shouted.

        “Shoo!”

        Erin hurled the rock. It missed the Goblin’s head. And the hill. But the green midget took the hint and disappeared in an instant. Erin sighed to herself.

        “Great. They’re like cockroaches. Evil, giant, green cockroaches. With teeth. And sharp knives. And red eyes.”

        She wondered what she should do. Then she thought about what she could actually do.

        Erin kept walking.

        The city kept getting larger the further she walked. She felt at some point it should stop getting bigger, but soon the buildings loomed in her vision. They were no skyscrapers, but they were taller than she felt medieval buildings should be. But the city was still far away. So she walked.

        And she was being watched. Multiple pairs of eyes stared at the young woman as she walked through the grass. They watched her for signs of weakness, for things that could be exploited. She was watched. Occasionally she turned around and threw a stone.

         

        —-

         

        When Erin got to the city gates she stared up for a while.

        “That’s a big wall.”

        It was a big understatement. The wall was high. And that was high even by wall standards. It was nearly forty feet tall, which Erin had no way of knowing was perfectly normal for a wall. She had no way of knowing it was forty feet tall either. She just thought it was big.

        But what was unusual about this particular wall, and what Erin did notice was the way the gate was constructed. It was no iron grating of a portcullis with handy holes to shoot and poke at enemies, but two solid metal doors. Erin wondered why, as the gates looked solid and hard to budge. They were, and for a reason. But she didn’t find out that reason until much later.

        Erin approached the gate. There wasn’t really anyone else going through at the moment, so she felt very alone and small as she walked up to them. She stopped when she saw the guard.

        He was big. He was armored. He was also a Drake, and he had yellow scales rather than green ones. Pale yellow, so Erin was reminded of popcorn. He also had a curved sword, and so it was with trepidation that she approached.

        “…Hi.”

        The Drake flicked his eyes down towards Erin and then resumed looking off into the distance. He was holding a spear at his side and a metal buckler on his left arm. Since he wasn’t using either to bash her to death, Erin considered this to be a good first start.

        “Um. Nice weather, isn’t it?”

        Again, the guard glanced at her. Again, he didn’t respond.

        “…Right. It’s just that I’m new here. And I’m Human. Nice to meet you. My name is Erin. I uh, know another guy who works with you. Relc? And Klb…Klb…the insect guy? So yeah. They know me. I’m no threat. And uh, I saw some Goblins running around a while back. They’re not here right now, but I felt you should know.”

        The Drake sighed audibly. And loudly.

        “Go on in, Human. Anyone can enter the city.”

        “Right. Thanks. Uh, have a nice day!”

        Erin smiled. He didn’t smile back.

        “I’ll just be going. Now.”

        She walked past the guard. As she walked through the iron gates she heard him mutter under his breath.

        “Humans.”

        Erin’s smile froze a bit on her face but she kept walking as if she’d heard nothing. Everyone was grumpy when they had to stand and deal with obnoxious tourists. And besides, he was just a guard. She walked through the imposing gates into the city. And then she had to stop.

        Because she had entered Liscor. A city of the fiery Drakes, built with the help of the industrious Antinium. Home to the prideful Gnolls and the occasional Beastkin, not to be confused with one another. Visited by many races, home to countless more. And now entering—

        One human.
    """.trimIndent()
    println(StringTokenizer(asd).countTokens())
}